package com.cs.web.aspect;

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
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static com.cs.sp.common.WebAssert.expect;
import static com.cs.sp.common.WebAssert.throwBizException;

/**
 * 本地锁降级切面
 * 当没有RedissonClient时生效
 */
@Aspect
@Slf4j
public class LocalLockAspect {

    // 本地锁缓存
    private final Map<String, ReentrantLock> lockMap = new ConcurrentHashMap<>();

    private final ExpressionParser parser = new SpelExpressionParser();
    private final DefaultParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();

    public LocalLockAspect() {
        log.info("LocalLockAspect used");
    }

    @Around("@annotation(distributedLock)")
    public Object around(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {
        // 构建锁key
        String lockKey = buildLockKey(joinPoint, distributedLock);

        // 获取本地锁
        ReentrantLock lock = lockMap.computeIfAbsent(lockKey, k -> new ReentrantLock());
        boolean isLocked = false;

        try {
            long waitTime = distributedLock.waitTime();
            TimeUnit unit = distributedLock.unit();

            if (waitTime < 0) {
                // 一直等待
                lock.lock();
                isLocked = true;
            } else if (waitTime == 0) {
                // 立即尝试
                isLocked = lock.tryLock();
            } else {
                // 等待指定时间
                isLocked = lock.tryLock(waitTime, unit);
            }

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
                // 如果锁没有被持有，从缓存中移除
                if (!lock.isLocked()) {
                    lockMap.remove(lockKey);
                }
            }
        }
    }

    /**
     * 构建锁key（与DistributedLockAspect相同）
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