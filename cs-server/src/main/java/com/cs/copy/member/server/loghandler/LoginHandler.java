package com.cs.copy.member.server.loghandler;

import com.cs.copy.system.api.base.BaseLog;
import com.cs.copy.system.api.enums.LogType;
import com.cs.copy.system.api.loghandler.LogHandler;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * @authro fun
 * @date 2025/6/18 02:12
 */
@Component
public class LoginHandler implements LogHandler {
    @Override
    public LogType supportedType() {
        return LogType.LOGIN;
    }

    @Override
    public BaseLog create(Object[] args, MethodSignature signature, JoinPoint joinPoint) {
        return new LoginLog();
    }

    @Override
    public void onSuccess(BaseLog log, Object result, Object[] args, MethodSignature signature, JoinPoint joinPoint) {

    }

    @Override
    public void onError(BaseLog log, Throwable e, Object[] args, MethodSignature signature, ProceedingJoinPoint jp) {

    }

    @Override
    public void complete(BaseLog log) {

    }
}
