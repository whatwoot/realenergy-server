package com.cs.web.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiter {
    String key() default "";          // 限流key前缀
    int limit() default 10;           // 限制次数
    int window() default 60;          // 窗口大小(秒)
    int expireBuffer() default 10;    // 新增：过期缓冲时间(秒)
    boolean useLoginId() default false; // 使用用户id
    boolean useIp() default true; // 使用ip
    String message() default "sp.common.frequency";
}