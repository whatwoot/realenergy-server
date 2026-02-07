package com.cs.web.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁注解
 * 支持SpEL表达式，支持两种锁获取策略：
 * 1. waitTime > 0：等待获取锁
 * 2. waitTime = 0：立即返回失败
 * 3. waitTime < 0：一直等待
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DistributedLock {
    
    /**
     * 锁的key，支持SpEL表达式
     * 示例：
     * #userId
     * #user.id
     * #p0.id (第一个参数的id属性)
     * T(com.example.util.Md5Util).md5(#user.name)
     */
    String key() default "";
    
    /**
     * 锁前缀
     */
    String prefix() default "__:lock:";

    /**
     * 等待时间
     * waitTime > 0：等待指定时间
     * waitTime = 0：立即返回
     * waitTime < 0：一直等待
     */
    long waitTime() default 5;
    
    /**
     * 锁持有时间（秒），超时自动释放
     */
    long leaseTime() default 10;
    
    /**
     * 时间单位
     */
    TimeUnit unit() default TimeUnit.SECONDS;

    boolean useLoginId() default false; // 使用用户id
    boolean useIp() default true; // 使用ip
    
    /**
     * 获取锁失败时的提示信息
     */
    String message() default "sp.common.frequency";
}