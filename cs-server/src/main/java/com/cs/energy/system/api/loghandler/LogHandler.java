package com.cs.energy.system.api.loghandler;

import com.cs.energy.system.api.base.BaseLog;
import com.cs.energy.system.api.enums.LogType;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

public interface LogHandler {
    LogType supportedType();

    BaseLog create(Object[] args, MethodSignature signature, JoinPoint joinPoint);

    void onSuccess(BaseLog log, Object result, Object[] args, MethodSignature signature, JoinPoint joinPoint);

    void onError(BaseLog log, Throwable e, Object[] args, MethodSignature signature, ProceedingJoinPoint jp);

    void complete(BaseLog log);
}