package com.cs.web.redis;

import com.cs.web.redis.base.RedisLockObj;
import com.cs.web.redis.base.RedisObj;

import java.util.concurrent.TimeUnit;

/**
 * @author sb
 * @date 2024/5/20 19:35
 */
public interface RedisService {


    /**
     * 取出一个对象
     *
     * @param key
     * @param <T>
     */
    <T> T get(String key, Class<T> clazz);

    /**
     * 存入一个对象
     *
     * @param obj
     * @param <T>
     */
    <T extends RedisObj> void set(T obj);

    /**
     * 取出一个对象
     *
     * @param obj
     * @param <T>
     */
    <T extends RedisObj> T get(T obj);


    <T extends RedisObj> void hashDel(Class<T> tClass, String objKey);
    /**
     * 获取一个hash数据
     *
     * @param clazz
     * @param objKey
     * @param <T>
     * @return
     */
    <T extends RedisObj> T hashGet(Class<T> clazz, String objKey);

    /**
     * 存入整个hash数据
     *
     * @param obj
     * @param <T>
     */
    <T extends RedisObj> void hashSet(T obj);

    /**
     * 更新单个field
     *
     * @param obj
     * @param field
     * @param <T>
     */
    <T extends RedisObj> void hashUpdate(T obj, String... field);


    /**
     * 获得一个锁
     *
     * @param lock
     * @param second
     * @param <T>
     * @return
     */
    <T extends RedisLockObj> boolean lock(T lock, long second);
    <T extends RedisLockObj> boolean lock(T lock, long time, TimeUnit unit);

    /**
     * unlock
     *
     * @param lock
     * @param <T>
     */
    <T extends RedisObj> void unlock(T lock);
}
