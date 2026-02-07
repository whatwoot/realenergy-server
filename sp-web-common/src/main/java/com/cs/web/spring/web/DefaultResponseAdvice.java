package com.cs.web.spring.web;

import com.cs.sp.common.Result;
import com.cs.sp.util.JsonUtil;
import com.cs.web.common.ErrorResult;
import com.cs.web.spring.config.MvcConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Method;

/**
 * TODO: 动态配置basePackages，这样才可以做通用jar
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(MvcConfig.class)
@ConditionalOnBean(MvcConfig.class)
@RestControllerAdvice(basePackages = {"com.ywlx","com.cs"})
@Slf4j
@Order(0)
public class DefaultResponseAdvice implements ResponseBodyAdvice<Object> {

    public DefaultResponseAdvice() {
        log.info("{} init", this.getClass().getSimpleName());
    }


    /**
     * 是否支持advice功能
     * true=支持，false=不支持
     */
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterClazz) {
        log.debug("ResponseHandler support {}", returnType.getDeclaringClass().getName());
        Method method = returnType.getMethod();
        // 通过在controller上主动添加注解标示该访问不用添加响应
        return !method.isAnnotationPresent(IgnoreResBody.class);
    }

    /**
     * 处理response的具体业务方法
     */
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        log.debug("ResponseHandler body {}", body);
        if (body instanceof Void) {
            return Result.ok();
        } else if (body instanceof Result) {
            return body;
        } else if (body instanceof ErrorResult) {
            ErrorResult errorResult = (ErrorResult) body;
            // 错误响应码
            response.setStatusCode(HttpStatus.valueOf(errorResult.getStatus()));
            return Result.fail(errorResult.getCode(), errorResult.getMsg());
        } else if (body instanceof String || StringHttpMessageConverter.class.equals(selectedConverterType)) {
            // body = null 时，但是需要返回String，则使用该方法
            response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            return JsonUtil.object2Json(Result.ok().data(body));
        }
        return Result.ok().data(body);
    }

}

