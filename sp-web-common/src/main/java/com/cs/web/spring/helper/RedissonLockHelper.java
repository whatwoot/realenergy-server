package com.cs.web.spring.helper;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static com.cs.sp.common.WebAssert.throwBizException;

/**
 * @authro fun
 * @date 2025/10/6 18:42
 */
@Slf4j
public class RedissonLockHelper {

    private RedissonClient redissonClient;

    public RedissonLockHelper(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
        log.info("RedissonLockHelper init");
    }

    public <T> T withLock(String lockKey, long waitTime, long leaseTime,
                          TimeUnit unit, Supplier<T> supplier) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            boolean isLocked = lock.tryLock(waitTime, leaseTime, unit);
            if (isLocked) {
                return supplier.get();
            } else {
                throwBizException("chk.lock.acquireFailed");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throwBizException("chk.lock.interrupted");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return null;
    }

    public void withLock(String lockKey, long waitTime, long leaseTime,
                         TimeUnit unit, Runnable runnable) {
        withLock(lockKey, waitTime, leaseTime, unit, () -> {
            runnable.run();
            return null;
        });
    }
}
