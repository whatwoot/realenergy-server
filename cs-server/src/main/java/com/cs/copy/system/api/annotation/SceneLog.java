package com.cs.copy.system.api.annotation;

import com.cs.copy.system.api.enums.LogType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SceneLog {
    LogType value(); // 日志类型
}