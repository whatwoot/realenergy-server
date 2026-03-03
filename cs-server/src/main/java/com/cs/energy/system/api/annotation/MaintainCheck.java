package com.cs.energy.system.api.annotation;

import com.cs.energy.global.constants.CacheKey;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author fiona
 * @date 2024/12/10 23:42
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MaintainCheck {
    String key() default CacheKey.GAME_STAUS;
}
