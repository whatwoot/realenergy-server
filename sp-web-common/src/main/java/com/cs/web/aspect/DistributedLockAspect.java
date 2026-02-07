package com.cs.web.aspect;

/**
 * @authro fun
 * @date 2026/1/2 04:18
 */

import cn.hutool.core.util.StrUtil;
import com.cs.web.annotation.DistributedLock;
import com.cs.web.jwt.JwtUser;
import com.cs.web.jwt.JwtUserHolder;
import com.cs.web.util.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

import static com.cs.sp.common.WebAssert.expect;
import static com.cs.sp.common.WebAssert.throwBizException;

/**
 * Redisson分布式锁切面
 * 当存在RedissonClient时生效
 */
@Aspect
@Slf4j
public class DistributedLockAspect {

    private RedissonClient redissonClient;

    public DistributedLockAspect(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
        log.info("DistributedLockAspect used");
    }

    private final ExpressionParser parser = new SpelExpressionParser();
    private final DefaultParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();

    @Around("@annotation(distributedLock)")
    public Object around(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {
        // 构建锁key
        String lockKey = buildLockKey(joinPoint, distributedLock);

        // 获取锁
        RLock lock = redissonClient.getLock(lockKey);
        boolean isLocked = false;

        try {
            long waitTime = distributedLock.waitTime();
            long leaseTime = distributedLock.leaseTime();
            TimeUnit unit = distributedLock.unit();

            if (waitTime < 0) {
                // 一直等待
                lock.lock(leaseTime, unit);
                isLocked = true;
            } else {
                // 尝试获取锁（可能等待或立即返回）
                isLocked = lock.tryLock(waitTime, leaseTime, unit);
            }

            // 获取锁失败，返回提示信息
            expect(isLocked, distributedLock.message());

            // 执行业务方法
            return joinPoint.proceed();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throwBizException("");
            return null;
        } finally {
            // 释放锁
            if (isLocked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 构建锁key
     */
    private String buildLockKey(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) {
        String prefix = distributedLock.prefix();

        StringJoiner joiner = new StringJoiner(":");
        joiner.add(prefix);

        if (distributedLock.useIp()) {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            joiner.add(IpUtils.getIpAddr(request));
        }

        if (distributedLock.useLoginId()) {
            JwtUser jwtUser = JwtUserHolder.get();
            if (jwtUser != null) {
                joiner.add(jwtUser.getId().toString());
            }
        }

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        if(StringUtils.hasText(distributedLock.key())) {
            try {
                Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
                String[] paramNames = discoverer.getParameterNames(method);
                Object[] args = joinPoint.getArgs();

                EvaluationContext context = new StandardEvaluationContext();
                if (paramNames != null) {
                    for (int i = 0; i < paramNames.length; i++) {
                        context.setVariable(paramNames[i], args[i]);
                    }
                }

                for (int i = 0; i < args.length; i++) {
                    context.setVariable("p" + i, args[i]);
                    context.setVariable("a" + i, args[i]);
                }

                Expression expression = parser.parseExpression(distributedLock.key());
                Object value = expression.getValue(context);

                if (value == null) {
                    log.warn("lock SpEL fail to default");
                    joiner.add(signature.getDeclaringType().getSimpleName())
                            .add(signature.getMethod().getName());
                } else {
                    joiner.add(value.toString());
                }
            } catch (Exception e) {
                log.error(StrUtil.format("lock-SpEL-fail: {}", distributedLock.key()), e);
                throwBizException("sp.lock.spelFail");
            }
        }else{
            joiner.add(signature.getDeclaringType().getSimpleName())
                    .add(signature.getMethod().getName());
        }

        return joiner.toString();
    }
}