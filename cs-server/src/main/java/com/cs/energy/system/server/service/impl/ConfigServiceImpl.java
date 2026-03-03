package com.cs.energy.system.server.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cs.energy.system.api.event.AfterBuildCacheEvent;
import com.cs.sp.enums.YesNoByteEnum;
import com.cs.energy.global.constants.CacheKey;
import com.cs.energy.system.api.constant.ConfigKey;
import com.cs.energy.system.api.entity.Config;
import com.cs.energy.system.api.event.ReBuildCacheEvent;
import com.cs.energy.system.api.event.RefreshConfigEvent;
import com.cs.energy.system.api.service.ConfigService;
import com.cs.energy.system.server.mapper.ConfigMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author gpthk
 * @since 2023-11-13
 */
@Slf4j
@Service
public class ConfigServiceImpl extends ServiceImpl<ConfigMapper, Config> implements ConfigService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public <T> T getGlobalConfig(Class<T> clazz) {
        JSONObject json = getJsonByCategory(ConfigKey.CATE_CONFIG);
        return json.toJavaObject(clazz);
    }

    @PostConstruct
    public void init() {

    }

    /**
     * 建立配置缓存缓存
     *
     * @param event
     */
    @EventListener
    public void refresh(ReBuildCacheEvent event) {
        log.info("Config-cache refresh All");
        refresh();
    }

    @EventListener
    public void refresh(RefreshConfigEvent event) {
        log.info("Config-cache refresh");
        refresh();
    }

    /**
     * 暂时只有通过category建立缓存的机制
     */
    public void refresh() {
        getByCategory(ConfigKey.CATE_CONFIG, true);
        SpringUtil.publishEvent(new AfterBuildCacheEvent(this));
    }

    @Override
    public List<Config> getOriByCategory(String category) {
        return getBaseMapper().selectList(new QueryWrapper<Config>().lambda()
                .eq(Config::getCategory, category)
                .orderByAsc(Config::getSeq)
                .orderByDesc(Config::getWeight));
    }

    @Override
    public List<Config> getByCategory(String category, boolean force) {
        String cacheKey = CacheKey.CONFIG_CATE_CACHE + category;
        if (!force) {
            Object cache = stringRedisTemplate.opsForValue().get(cacheKey);
            if (cache != null) {
                return JSONArray.parseArray(cache.toString(), Config.class);
            }
        }
        List<Config> list = getOriByCategory(category);
        stringRedisTemplate.opsForValue().set(cacheKey, JSONArray.toJSONString(list));
        return list;
    }

    @Override
    public List<Config> getByCategory(String category) {
        return getByCategory(category, false);
    }

    @Override
    public JSONObject getShowJsonByCategory(String category) {
        List<Config> list = getByCategory(category);
        if (list.isEmpty()) {
            return null;
        }
        JSONObject json = new JSONObject();
        for (Config config : list) {
            if (YesNoByteEnum.YES.eq(config.getShowed())) {
                json.put(config.getConfigKey(), config.getConfigValue());
            }
        }
        return json;
    }

    @Override
    public JSONObject getJsonByCategory(String category) {
        List<Config> list = getByCategory(category);
        if (list.isEmpty()) {
            return null;
        }
        JSONObject json = new JSONObject();
        for (Config config : list) {
            json.put(config.getConfigKey(), config.getConfigValue());
        }
        return json;
    }

    @Override
    public <T> T getObjByCategory(String category, Class<T> clazz) {
        JSONObject json = getJsonByCategory(category);
        return json == null ? null : json.toJavaObject(clazz);
    }


    @Override
    public Config getByKey(String key, boolean force) {
        String cacheKey = CacheKey.CONFIG_KEY_CACHE + key;
        if (!force) {
            Object cache = stringRedisTemplate.opsForValue().get(cacheKey);
            if (cache != null) {
                return JSONObject.parseObject(cache.toString(), Config.class);
            }
        }
        Config config = getOriByKey(key);
        // 这里如果config是null会变成"null"字符串存到redis
        // 反序列化时，如果为"null"也会被解析成null值
        stringRedisTemplate.opsForValue().set(cacheKey, JSONObject.toJSONString(config));
        return config;
    }

    @Override
    public Config getOriByKey(String key) {
        return getBaseMapper().selectOne(new QueryWrapper<Config>().lambda()
                .eq(Config::getConfigKey, key)
                .orderByDesc(Config::getWeight)
                .last("limit 1")
        );
    }

    @Override
    public Config getByKey(String key) {
        return getByKey(key, false);
    }

    @Override
    public Config getOriByCategoryAndKey(String category, String key) {
        return getBaseMapper().selectOne(new QueryWrapper<Config>().lambda()
                .eq(Config::getCategory, category)
                .eq(Config::getConfigKey, key)
                .orderByDesc(Config::getWeight)
                .last("limit 1")
        );
    }

    @Override
    public Config getByCategoryAndKey(String category, String key) {
        return getByCategoryAndKey(category, key, false);
    }

    @Override
    public Config getByCategoryAndKey(String category, String key, boolean force) {
        String cacheKey = String.format("%s%s:%s", CacheKey.CONFIG_CATE_KEY_CACHE, category, key);
        if (!force) {
            Object cache = stringRedisTemplate.opsForValue().get(cacheKey);
            if (cache != null) {
                return JSONObject.parseObject(cache.toString(), Config.class);
            }
        }
        Config config = getOriByCategoryAndKey(category, key);
        // 这里如果config是null会变成"null"字符串存到redis
        // 反序列化时，如果为"null"也会被解析成null值
        stringRedisTemplate.opsForValue().set(cacheKey, JSONObject.toJSONString(config));
        return config;
    }

    @Override
    public String getValueByCategoryAndKey(String category, String key) {
        Config config = getByCategoryAndKey(category, key);
        if (config == null) {
            return null;
        }
        return config.getConfigValue();
    }

    @Override
    public List<String> getListByCategory(String category) {
        List<String> configs = new ArrayList<>();
        List<Config> configList = getByCategory(category);
        for (Config config : configList) {
            configs.add(config.getSeq(), config.getConfigValue());
        }
        return configs;
    }

    @Override
    public List<Integer> getIntListByCategory(String category) {
        List<String> list = getListByCategory(category);
        return list.stream().map(v -> v == null ? null : Integer.parseInt(v)).collect(Collectors.toList());
    }

    @Override
    public List<Long> getLongListByCategory(String category) {
        List<String> list = getListByCategory(category);
        return list.stream().map(v -> v == null ? null : Long.parseLong(v)).collect(Collectors.toList());
    }

    @Override
    public List<BigDecimal> getDecimalListByCategory(String category) {
        List<String> list = getListByCategory(category);
        return list.stream().map(v -> v == null ? null : new BigDecimal(v)).collect(Collectors.toList());
    }

    @Override
    public <T> List<T> getObjListByCategory(String category, Class<T> clazz) {
        List<String> list = getListByCategory(category);
        return list.stream().map(v -> JSONObject.parseObject(v, clazz)).collect(Collectors.toList());
    }

    @Override
    public String getValueByKey(String key) {
        Config config = getByKey(key);
        return config == null ? null : config.getConfigValue();
    }

    @Override
    public Integer getIntByKey(String key) {
        String value = getValueByKey(key);
        return value == null ? null : Integer.parseInt(value);
    }

    @Override
    public BigDecimal getBigDecimalByKey(String key) {
        String value = getValueByKey(key);
        return value == null ? null : new BigDecimal(value);
    }

    @Override
    public Long getLongByKey(String key) {
        String value = getValueByKey(key);
        return value == null ? null : Long.parseLong(value);
    }

    @Override
    public JSONObject getJsonByKey(String key) {
        String value = getValueByKey(key);
        return value == null ? null : JSONObject.parseObject(value);
    }

    @Override
    public <T> T getObjByKey(String key, Class<T> clazz) {
        String value = getValueByKey(key);
        return value == null ? null : JSONObject.parseObject(value, clazz);
    }

}
