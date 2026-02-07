package com.cs.web.aspect;

import com.cs.sp.common.HttpStatus;
import com.cs.sp.constant.Constant;
import com.cs.web.annotation.RateLimiter;
import com.cs.web.jwt.JwtUser;
import com.cs.web.jwt.JwtUserHolder;
import com.cs.web.util.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

import static com.cs.sp.common.WebAssert.throwException;


/**
 * 限速切面，在MaintainCheck后执行
 */
@Slf4j
@Aspect
@Order(101)
public class RateLimiterAspect {
    private final RedisTemplate<String, Object> redisTemplate;

    public RateLimiterAspect(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // Lua脚本实现原子操作
    private static final String RATE_LIMITER_SCRIPT =
            "local key = KEYS[1]\n" +
                    "local now = tonumber(ARGV[1])\n" +
                    "local window = tonumber(ARGV[2])\n" +
                    "local max = tonumber(ARGV[3])\n" +
                    "local expireBuffer = tonumber(ARGV[4] or 10) \n" +
                    "redis.call('ZREMRANGEBYSCORE', key, 0, now - window)\n" +
                    "local current = redis.call('ZCARD', key)\n" +
                    "if current < max then\n" +
                    "    redis.call('ZADD', key, now, now .. '_' .. math.random(1000))\n" +
                    "    redis.call('EXPIRE', key, window + expireBuffer) \n" +
                    "    return 1\n" +
                    "else\n" +
                    "    return 0\n" +
                    "end";

    @Pointcut("@annotation(com.cs.web.annotation.RateLimiter)")
    public void rateLimiterPointcut() {
    }

    @Around("rateLimiterPointcut() && @annotation(rateLimiter)")
    public Object around(ProceedingJoinPoint joinPoint, RateLimiter rateLimiter) throws Throwable {
        // 获取方法签名信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 构建限流key
        String key = buildKey(joinPoint, rateLimiter);

        // 获取当前时间戳(秒)
        long now = System.currentTimeMillis() / 1000;

        // 执行Lua脚本
        Long result = redisTemplate.execute(
                (RedisCallback<Long>) connection -> connection.eval(
                        RATE_LIMITER_SCRIPT.getBytes(),
                        ReturnType.INTEGER,
                        1,
                        key.getBytes(),
                        String.valueOf(now).getBytes(),
                        String.valueOf(rateLimiter.window()).getBytes(),
                        String.valueOf(rateLimiter.limit()).getBytes(),
                        String.valueOf(rateLimiter.expireBuffer()).getBytes()
                )
        );

        if (!Constant.ONE_LONG.equals(result)) {
            log.warn("RateLimiter key={}, method={}", key, method.getName());
            throwException(HttpStatus.TOO_MANY_REQUESTS, rateLimiter.message());
        }

        return joinPoint.proceed();
    }

    private String buildKey(ProceedingJoinPoint joinPoint, RateLimiter rateLimiter) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        StringBuilder key = new StringBuilder(com.cs.web.common.Constant.RATELIMIT_LOCK);

        if (rateLimiter.useLoginId()) {
            JwtUser jwtUser = JwtUserHolder.get();
            key.append(jwtUser != null ? jwtUser.getId() : 0);
            key.append(":");
        }

        // 使用注解中的key或默认使用类名+方法名
        if (!rateLimiter.key().isEmpty()) {
            key.append(rateLimiter.key());
        } else {
            key.append(method.getDeclaringClass().getName())
                    .append(".")
                    .append(method.getName());
        }

        if (rateLimiter.useIp()) {
            // 添加客户端IP作为区分
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            key.append(":").append(IpUtils.getIpAddr(request));
        }

        return key.toString();
    }


}