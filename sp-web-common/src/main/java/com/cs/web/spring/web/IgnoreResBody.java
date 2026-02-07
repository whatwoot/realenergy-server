package com.cs.web.spring.web;

import java.lang.annotation.*;

/**
 *
 * 用于统一响应处理的响应结果在特定场景时，需要返回原始响应值，不添加统一响应体处理
 *
 * @author sb
 * @see
 * @since 1.0
 */
@Documented
@Inherited
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface IgnoreResBody {

}
