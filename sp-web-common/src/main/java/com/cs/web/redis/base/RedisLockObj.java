package com.cs.web.redis.base;

/**
 * 锁对象
 * @author sb
 * @date 2024/5/21 00:44
 */
public interface RedisLockObj extends RedisObj{
    String lockValue();
}
