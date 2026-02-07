package com.cs.web.redis.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cs.sp.common.WebAssert;
import com.cs.web.redis.RedisService;
import com.cs.web.redis.base.RedisLockObj;
import com.cs.web.redis.base.RedisObj;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author sb
 * @date 2024/5/20 19:41
 */
@Slf4j
public class RedisServiceImpl implements RedisService {

    private RedisTemplate redisTemplate;
    private StringRedisTemplate stringRedisTemplate;

    private final Map<Class<?>, Map<String, Field>> hashObjFieldMap = new ConcurrentHashMap<>();

    public RedisServiceImpl(RedisTemplate redisTemplate, StringRedisTemplate stringRedisTemplate) {
        this.redisTemplate = redisTemplate;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    private Map<String, Field> getObjectFields(Class<?> clazz) {
        return hashObjFieldMap.computeIfAbsent(clazz, (k) -> {
            ConcurrentHashMap<String, Field> map = new ConcurrentHashMap<>();
            Class<?> sClazz = k;
            while (sClazz != Object.class) {
                Field[] fields = sClazz.getDeclaredFields();
                sClazz = sClazz.getSuperclass();
                if (fields.length > 0) {
                    for (Field f : fields) {
                        if (Modifier.isStatic(f.getModifiers())) {
                            continue;
                        }
                        if (Modifier.isTransient(f.getModifiers())) {
                            continue;
                        }
                        if (Modifier.isFinal(f.getModifiers())) {
                            continue;
                        }
                        f.setAccessible(true);
                        map.put(f.getName(), f);
                    }
                }
            }
            return map;
        });
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        WebAssert.isNotBlank(key, "chk.redis.required", "obj");
        String s = stringRedisTemplate.opsForValue().get(key);
        if (s == null) {
            return null;
        }
        return JSONUtil.toBean(s, clazz);
    }

    @Override
    public <T extends RedisObj> void set(T obj) {
        WebAssert.isNotNull(obj, "chk.redis.required", "obj");
        BoundValueOperations<String, String> strOps = stringRedisTemplate.boundValueOps(obj.key());
        strOps.set(JSONUtil.toJsonStr(obj));
    }

    @Override
    public <T extends RedisObj> T get(T obj) {
        WebAssert.isNotNull(obj, "chk.redis.required", "obj");
        String res = stringRedisTemplate.opsForValue().get(obj.key());
        if (res == null) {
            return null;
        }
        return JSONUtil.toBean(res, (Class<T>) obj.getClass());
    }

    /**
     * hash的key
     *
     * @param obj
     * @return
     */
    private String getRedisKeyForHash(RedisObj obj) {
        return getRedisKeyForHash(obj.getClass(), obj.key());
    }

    private String getRedisKeyForHash(Class<?> clazz, String objectKey) {
        return String.format("%s#%s", clazz.getSimpleName(), objectKey);
    }

    @Override
    public <T extends RedisObj> void hashDel(Class<T> clazz, String objKey) {
        redisTemplate.delete(getRedisKeyForHash(clazz, objKey));
    }

    @Override
    public <T extends RedisObj> T hashGet(Class<T> clazz, String objKey) {
        WebAssert.isNotBlank(objKey, "chk.redis.required", "key");
        Map<String, Field> map = getObjectFields(clazz);
        if (CollUtil.isNotEmpty(map)) {
            Map<Object, Object> value = redisTemplate.opsForHash().entries(getRedisKeyForHash(clazz, objKey));
            if (CollUtil.isNotEmpty(value)) {
                return new JSONObject(value).toBean(clazz);
            }
        }
        return null;
    }

    @Override
    public <T extends RedisObj> void hashSet(T obj) {
        Map<String, Field> map = getObjectFields(obj.getClass());
        if (CollUtil.isEmpty(map)) {
            return;
        }
        Map<String, Object> dt = new HashMap<>();
        Object value;
        for (Map.Entry<String, Field> entry : map.entrySet()) {
            try {
                value = entry.getValue().get(obj);
                if (value != null) {
                    dt.put(entry.getKey(), value);
                }
            } catch (Throwable e) {
                log.warn(StrUtil.format("hashSet set field {} failed: {}",
                        entry.getKey(), JSONUtil.toJsonStr(obj)), e);
            }
        }
        if (dt.size() > 0) {
            redisTemplate.opsForHash().putAll(getRedisKeyForHash(obj), dt);
        }
    }

    @Override
    public <T extends RedisObj> void hashUpdate(T obj, String... fieldNames) {
        WebAssert.expectNotNull(fieldNames, "chk.redis.required", "field");
        WebAssert.expectGt0(fieldNames.length, "chk.redis.required", "field");
        Map<String, Field> map = getObjectFields(obj.getClass());
        Map<String, Object> putAll = new HashMap<>();

        String key = getRedisKeyForHash(obj);
        List<String> delFields = new ArrayList<>();
        try {
            Object value;
            Field field;
            for (String fieldName : fieldNames) {
                field = map.get(fieldName);
                WebAssert.expectNotNull(field, "chk.redis.invalid", fieldName);
                value = field.get(obj);
                if (value == null) {
                    delFields.add(fieldName);
                } else {
                    putAll.put(fieldName, value);
                }
            }
            if (!delFields.isEmpty()) {
                redisTemplate.opsForHash().delete(key, delFields.toArray(new String[0]));
            }
            if (!putAll.isEmpty()) {
                redisTemplate.opsForHash().putAll(key, putAll);
            }
        } catch (IllegalAccessException e) {
            log.error(StrUtil.format("更新Hash值失败:{} {}", JSONUtil.toJsonStr(obj), fieldNames), e);
        }
    }

    @Override
    public <T extends RedisLockObj> boolean lock(T lock, long second, TimeUnit unit) {
        WebAssert.isNotNull(lock, "chk.redis.required", "lock");
        String lockKey = getRedisKeyForHash(lock);
        WebAssert.isNotBlank(lockKey, "chk.redis.required", "key");
        WebAssert.isNotNull(lock.lockValue(), "chk.redis.required", "lockValue");
        WebAssert.isNotNull(unit, "chk.redis.required", "unit");
        return stringRedisTemplate.opsForValue().setIfAbsent(lockKey, lock.lockValue(), second, TimeUnit.SECONDS);
    }

    @Override
    public <T extends RedisLockObj> boolean lock(T lock, long second) {
        return lock(lock, second, TimeUnit.SECONDS);
    }

    @Override
    public <T extends RedisObj> void unlock(T lock) {
        String lockKey = getRedisKeyForHash(lock);
        stringRedisTemplate.delete(lockKey);
    }
}
