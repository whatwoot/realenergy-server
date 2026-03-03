package com.cs.energy.system.api.aspect;

import cn.hutool.extra.spring.SpringUtil;
import com.cs.energy.system.api.annotation.SceneLog;
import com.cs.energy.system.api.base.BaseLog;
import com.cs.energy.system.api.enums.LogType;
import com.cs.energy.system.api.loghandler.LogHandler;
import com.cs.sp.common.exception.BaseException;
import com.cs.sp.enums.YesNoByteEnum;
import com.cs.web.spring.config.i18n.I18nHelper;
import com.cs.web.util.IpUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Aspect
@Component
public class LogAspect {

    // 1. 使用ConcurrentMap预加载处理器（线程安全）
    private final Map<LogType, LogHandler> handlerMap;

    // 2. 构造器注入所有处理器
    @Autowired
    public LogAspect(List<LogHandler> handlers) {
        this.handlerMap = handlers.stream()
                .collect(Collectors.toConcurrentMap(
                        LogHandler::supportedType,  // Key: 日志类型
                        Function.identity()         // Value: 处理器实例
                ));
    }

    @Pointcut("execution(@com.cs.energy.system.api.annotation.SceneLog * *(..)) && @annotation(sceneLog)")
    public void sceneLogPointcut(SceneLog sceneLog) {}
    
    @Around("sceneLogPointcut(sceneLog)")
    public Object compileTimeAdvice(ProceedingJoinPoint jp, SceneLog sceneLog) throws Throwable {
        // 编译时织入的实现
        // 4. 直接从Map获取处理器（O(1)时间复杂度）
        LogHandler handler = handlerMap.get(sceneLog.value());
        if (handler == null) {
            return jp.proceed(); // 无对应处理器则跳过
        }
        // 5. 执行日志处理
        MethodSignature signature = (MethodSignature) jp.getSignature();
        Object[] args = jp.getArgs();
        BaseLog log = handler.create(args, signature, jp);
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        log.setIp(IpUtils.getIpAddr(attr.getRequest()));
        log.setUa(attr.getRequest().getHeader("User-Agent"));
        try {
            Object result = jp.proceed();
            log.setStatus(YesNoByteEnum.YES.getCode());
            handler.onSuccess(log, result, args, signature, jp);
            return result;
        }catch (BaseException e){
            log.setStatus(YesNoByteEnum.NO.getCode());
            log.setErrCode(e.getCode());
            log.setErrMsg(SpringUtil.getBean(I18nHelper.class).getMsg(e.getCode(), e.getArgs()));
            handler.onError(log, e, args, signature, jp);
            throw e;
        } catch (Throwable e) {
            log.setStatus(YesNoByteEnum.NO.getCode());
            log.setErrCode(e.getCause() == null ? e.getClass().getName() : e.getCause().getClass().getName());
            log.setErrMsg(StringUtils.truncate(e.getMessage(), 1000));
            handler.onError(log, e, args, signature, jp);
            throw e;
        }finally {
            handler.complete(log);
        }
    }
}