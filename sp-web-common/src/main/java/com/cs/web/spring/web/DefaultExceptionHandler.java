package com.cs.web.spring.web;

import cn.hutool.core.util.StrUtil;
import com.cs.sp.common.exception.BaseException;
import com.cs.sp.constant.Constant;
import com.cs.web.common.ErrorResult;
import com.cs.web.spring.config.MvcConfig;
import com.cs.web.spring.filter.FullCachingRequestWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 统一异常处理类
 * TODO: 动态配置basePackages，这样才可以做通用jar
 * 提了一个建议：https://github.com/spring-projects/spring-framework/issues/32776
 *
 * @author sb
 * @date 2018/9/7 01:41
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(MvcConfig.class)
@ConditionalOnBean(MvcConfig.class)
@RestControllerAdvice(basePackages = {"com.ywlx","com.cs"})
@Order(0)
public class DefaultExceptionHandler {

    private final MessageSource ms;

    public DefaultExceptionHandler(MessageSource ms) {
        log.info("{} init", this.getClass().getSimpleName());
        this.ms = ms;
    }

    /**
     * 错误信息展示顺序，越排在后面的错误类型，越优先展示
     */
    private static final List<String> ERROR_ORDER = new ArrayList<>(Arrays.asList("Email", "Pattern", "Past",
            "PastOrPresent", "FutureOrPresent", "Future", "Positive", "PositiveOrZero", "NegativeOrZero", "Negative",
            "Digits", "Size", "DecimalMin", "DecimalMax", "Min", "Max", "AssertFalse", "AssertTrue", "NotBlank",
            "NotEmpty", "Null", "NotNull"));

    /**
     * sse推送断开后，
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(value = {
            HttpMessageNotWritableException.class,
    })
    public final ErrorResult handleException(HttpMessageNotWritableException ex, NativeWebRequest request) {
        String code = "sp.common.sse";
        ErrorResult errorResult = new ErrorResult(HttpStatus.PRECONDITION_FAILED.value(), code, getMsg(code));
        logResult(errorResult, request, ex);
        return errorResult;
    }

    /**
     * 基本的请求错误，用于处理无法处理的请求
     * 请求Method不匹配(eg: GET POST)
     * 请求头的Accept不匹配
     * 不存在的请求
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(value = {
            // 不支持的请求类型
            HttpRequestMethodNotSupportedException.class,
            //基本的请求错误，用于处理无法处理的请求
            HttpMediaTypeNotSupportedException.class,
            // 不能处理的请求类型
            HttpMediaTypeNotAcceptableException.class,
            // 没有该请求
            NoHandlerFoundException.class,
    })
    public final ErrorResult handleException(ServletException ex, NativeWebRequest request) {
        ErrorResult errorResult = return404(ex);
        logResult(errorResult, request, ex);
        return errorResult;
    }

    /**
     * 参数有误，http解析失败
     * requestBody请求时，格式为非有效的json字符串时，报出的异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(value = {HttpMessageNotReadableException.class})
    public final ErrorResult handleException(HttpMessageNotReadableException ex, NativeWebRequest request) {
        String err = "sp.common.400";
        String msg = getMsg(err);
        ErrorResult errorResult = new ErrorResult(HttpStatus.BAD_REQUEST.value(), err, msg);
        logResult(errorResult, request, ex);
        return errorResult;
    }

    /**
     * 文件上传 出错
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(value = {MissingServletRequestPartException.class})
    public final ErrorResult handleException(MissingServletRequestPartException ex, NativeWebRequest request) {
        String err = "sp.common.fileItemError";
        String msg = getMsg(err);
        log.error(msg, ex);
        ErrorResult errorResult = new ErrorResult(HttpStatus.BAD_REQUEST.value(), err, msg);
        logResult(errorResult, request, ex);
        return errorResult;
    }


    /**
     * requireparam 字段缺失
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(value = {
            MissingServletRequestParameterException.class
    })
    public final ErrorResult handleException(MissingServletRequestParameterException ex, NativeWebRequest request) {
        String code = "chk.common.format";
        String msg = getMsg(code, ex.getParameterName());
        log.warn(ex.getMessage(), ex);
        ErrorResult errorResult = new ErrorResult(HttpStatus.BAD_REQUEST.value(), code, msg);
        logResult(errorResult, request, ex);
        return errorResult;
    }


    /**
     * requireparam 格式不匹配
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(value = {
            MethodArgumentTypeMismatchException.class
    })
    public final ErrorResult handleException(MethodArgumentTypeMismatchException ex, NativeWebRequest request) {
        String code = "chk.common.format";
        String msg = getMsg(code, ex.getName());
        ErrorResult errorResult = new ErrorResult(HttpStatus.BAD_REQUEST.value(), code, msg);
        logResult(errorResult, request, ex);
        return errorResult;
    }


    /**
     * 基本参数错误
     * 一般用于query和普通表单参数的绑定异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(value = {BindException.class})
    public final ErrorResult handleException(BindException ex, NativeWebRequest request) {
        // 按类中成员变量的声明顺序排序所有错误信息
        List<FieldError> errors = getSortedFieldErrors(ex);
        ErrorResult errorResult = parseFirstError(ex, errors);
        logResult(errorResult, request, ex);
        return errorResult;
    }

    /**
     * 基本参数错误
     * 一般用于body参数的绑定异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public final ErrorResult handleException(MethodArgumentNotValidException ex, NativeWebRequest request) {

        final BindingResult bindingResult = ex.getBindingResult();

        // 按类中成员变量的声明顺序排序所有错误信息
        List<FieldError> errors = getSortedFieldErrors(bindingResult);
        ErrorResult errorResult = parseFirstError(ex, errors);
        logResult(errorResult, request, ex);
        return errorResult;
    }


    /**
     * 基本的框架自定义错误
     * 包含：
     * CheckException、ServiceException、SysException
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(value = {BaseException.class})
    public final ErrorResult handleException(BaseException ex, NativeWebRequest request) {
        String msg = getMsg(ex.getCode(), ex.getArgs());
        ErrorResult errorResult = new ErrorResult(ex.getStatusCode(), ex.getCode(), msg);
        logResult(errorResult, request, ex);
        return errorResult;
    }


    /**
     * 数据库级别的防重复记录
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(value = {DuplicateKeyException.class})
    public final ErrorResult handleException(DuplicateKeyException ex, NativeWebRequest request) {
        int code = HttpStatus.BAD_REQUEST.value();
        String errCode = "chk.common.duplicate";
        String msg = getMsg(errCode);
        ErrorResult errorResult = new ErrorResult(code, errCode, msg);
        logResult(errorResult, request, ex);
        return errorResult;
    }

    /**
     * 系统异常，此时异常信息需要对外不可见
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(value = {Exception.class})
    public final Object handleException(Exception ex, NativeWebRequest request) {
        if (request.getHeader(com.cs.web.common.Constant.HEADER_ACCEPT) != null &&
                request.getHeader(com.cs.web.common.Constant.HEADER_ACCEPT)
                        .contains(com.cs.web.common.Constant.EVENT_STREAM)) {
            log.warn("SSE-final-ex: {}", ex.getMessage());
            return ResponseEntity.noContent().build(); // 返回204或直接关闭连接
        }
        String err = "sp.common.500";
        String msg = getMsg(err);
        ErrorResult errorResult = new ErrorResult(HttpStatus.INTERNAL_SERVER_ERROR.value(), err, msg);
        logResult(errorResult, request, ex);
        return errorResult;
    }

    /**
     * 通过对错误信息的排序，来达到每次请求错误提示一致的问题
     * 解决dto包含多个注解，并同时出错时，error信息排序随机的问题
     *
     * @param ex
     * @return
     */
    private List<FieldError> getSortedFieldErrors(BindingResult ex) {

        List<FieldError> errors = ex.getFieldErrors();

        Object dto = ex.getTarget();

        final Field[] declaredFields = dto.getClass().getDeclaredFields();

        List<String> fieldOrder = new ArrayList<>();

        for (Field field : declaredFields) {
            fieldOrder.add(field.getName());
        }

        errors = new ArrayList<>(errors);

        //按参数在DTO中定义的顺序，显示错误信息
        errors.sort((a, b) -> {
            if (!a.getField().equals(b.getField())) {
                return fieldOrder.indexOf(a.getField()) - fieldOrder.indexOf(b.getField());
            } else {
                return ERROR_ORDER.indexOf(b.getCode()) - ERROR_ORDER.indexOf(a.getCode());
            }
        });

        return errors;
    }

    private ErrorResult parseFirstError(Exception ex, List<FieldError> errors) {

        FieldError error = errors.get(0);

        String misMatch = "typeMismatch";

        String err;
        Object[] args;

        if (misMatch.equals(error.getCode())) {
            err = "chk.common.format";
            args = new Object[]{error.getField()};
        } else {
            err = error.getDefaultMessage();
            args = error.getArguments();
            // 第1个参数是DefaultMessageSourceResolvable，这个值对国际化无用处，所以替换掉他
            args[0] = error.getField();
        }

        String msg = getMsg(err, args);
        return new ErrorResult(HttpStatus.BAD_REQUEST.value(), err, msg);
    }


    /**
     * 不可处理的请求
     *
     * @param ex
     * @return
     */
    private ErrorResult return404(Exception ex) {
        String err = "sp.common.404";
        String msg = getMsg(err);
        return new ErrorResult(HttpStatus.NOT_FOUND.value(), err, msg);
    }

    /**
     * 国际化方法
     *
     * @param code
     * @param args
     * @return
     */
    protected String getMsg(String code, Object... args) {
        return ms.getMessage(code, args, LocaleContextHolder.getLocale());
    }

    private static void logResult(ErrorResult errorResult, NativeWebRequest request, Throwable ex) {
        HttpServletRequest req = request.getNativeRequest(HttpServletRequest.class);
        String param = Constant.EMPTY_STRING;
        String body = Constant.EMPTY_STRING;
        if (req.getQueryString() != null) {
            param = req.getQueryString();
        }

        if (req instanceof ContentCachingRequestWrapper) {
            body = new String(((ContentCachingRequestWrapper) req).getContentAsByteArray(), StandardCharsets.UTF_8);
        }
        if (req instanceof FullCachingRequestWrapper) {
            body = new String(((FullCachingRequestWrapper) req).getCachedContent(), StandardCharsets.UTF_8);
        }

        String ua = req.getHeader(com.cs.web.common.Constant.HEADER_USER_AGENT);
        String referer = req.getHeader(com.cs.web.common.Constant.HEADER_REFERER);
        HttpStatus httpStatus = HttpStatus.valueOf(errorResult.getStatus());
        if (HttpStatus.BAD_REQUEST.equals(httpStatus) || HttpStatus.UNAUTHORIZED.equals(httpStatus)) {
            log.info("URI {}, {}, e {}, ua {}, referer {}, param {}, body {}, {}", req.getRequestURI(), errorResult.getStatus(),
                    errorResult.getMsg(), ua, referer, param, body, ex == null ? null : ex.getMessage());
        } else if (httpStatus.is4xxClientError()) {
            log.warn("URI {}, {}, e {}, ua {}, referer {}, param {}, body {}, {}", req.getRequestURI(), errorResult.getStatus(),
                    errorResult.getMsg(), ua, referer, param, body, ex == null ? null : ex.getMessage());
        } else {
            log.error(StrUtil.format("URI {}, {}, e {}, ua {}, referer {}, param {}, body {}", req.getRequestURI(), errorResult.getStatus(),
                    errorResult.getMsg(), ua, referer, param, body), ex);
        }
    }
}
