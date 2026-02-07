package com.cs.sp.common;


import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Map;

/**
 * @author sb
 * @date 2018/12/31 17:43
 */
public abstract class WebAssert {

    /**
     * 用于参数验证的场景
     *
     * @param expression
     * @param code
     * @param args
     */
    public static void isTrue(boolean expression, String code, Object... args) {
        if (!expression) {
            throwParamException(code, args);
        }
    }

    /**
     * 用于参数验证的场景
     *
     * @param obj
     * @param code
     * @param args
     */
    public static void isNotBlank(String obj, String code, Object... args) {
        isTrue(StringUtils.hasText(obj), code, args);
    }

    /**
     * 用于参数验证的场景
     *
     * @param obj
     * @param code
     * @param args
     */
    public static void isBlank(String obj, String code, Object... args) {
        isTrue(!StringUtils.hasText(obj), code, args);
    }

    /**
     * 用于参数验证的场景
     *
     * @param obj
     * @param code
     * @param args
     */
    public static void isNotNull(Object obj, String code, Object... args) {
        isTrue(obj != null, code, args);
    }

    public static void isNull(Object obj, String code, Object... args) {
        isTrue(obj == null, code, args);
    }

    public static void isGt0(int result, String code, Object... args) {
        isTrue(result > 0, code, args);
    }

    public static void isGte0(int result, String code, Object... args) {
        isTrue(result >= 0, code, args);
    }

    public static void isLt0(int result, String code, Object... args) {
        isTrue(result < 0, code, args);
    }

    public static void isLte0(int result, String code, Object... args) {
        isTrue(result <= 0, code, args);
    }


    /**
     * 用于业务是否匹配的判断
     *
     * @param expression
     * @param code
     * @param args
     */
    public static void expect(boolean expression, String code, Object... args) {
        if (!expression) {
            throwBizException(code, args);
        }
    }

    public static void expectBlank(String obj, String code, Object... args){
        expect(!StringUtils.hasText(obj), code, args);
    }

    public static void expectNotBlank(String obj, String code, Object... args){
        expect(StringUtils.hasText(obj), code, args);
    }

    /**
     * 用于业务是否匹配的判断
     *
     * @param obj
     * @param code
     * @param args
     */
    public static void expectNull(Object obj, String code, Object... args) {
        expect(obj == null, code, args);
    }

    /**
     * 用于业务是否匹配的判断
     *
     * @param obj
     * @param code
     * @param args
     */
    public static void expectNotNull(Object obj, String code, Object... args) {
        expect(obj != null, code, args);
    }

    /**
     * 用于业务是否匹配的判断
     *
     * @param result
     * @param code
     * @param args
     */
    public static void expectGt0(int result, String code, Object... args) {
        expect(result > 0, code, args);
    }

    public static void expectGt0(long result, String code, Object... args) {
        expect(result > 0, code, args);
    }

    public static void expectGte0(int result, String code, Object... args) {
        expect(result >= 0, code, args);
    }

    public static void expectGte0(long result, String code, Object... args) {
        expect(result >= 0, code, args);
    }

    public static void expectLt0(int result, String code, Object... args) {
        expect(result < 0, code, args);
    }

    public static void expectLt0(long result, String code, Object... args) {
        expect(result < 0, code, args);
    }

    public static void expectLte0(int result, String code, Object... args) {
        expect(result <= 0, code, args);
    }

    public static void expectLte0(long result, String code, Object... args) {
        expect(result <= 0, code, args);
    }

    public static void expectEmpty(Map<?, ?> map, String code, Object... args) {
        expect(map.isEmpty(), code, args);
    }

    public static void expectNotEmpty(Map<?, ?> map, String code, Object... args) {
        expect(!map.isEmpty(), code, args);
    }

    public static void expectEmpty(Collection<?> collection, String code, Object... args) {
        expect(CollectionUtils.isEmpty(collection), code, args);
    }

    public static void expectNotEmpty(Collection<?> collection, String code, Object... args) {
        expect(!CollectionUtils.isEmpty(collection), code, args);
    }

    public static void needLogin(boolean expression) {
        if (!expression) {
            throwException(HttpStatus.UNAUTHORIZED, "auth.notLogin");
        }
    }

    /**
     * 401 未登录或登录过期
     *
     * @param expression
     * @param code
     * @param args
     */
    public static void needLogin(boolean expression, String code, Object... args) {
        if (!expression) {
            throwException(HttpStatus.UNAUTHORIZED, code, args);
        }
    }

    public static void hasPermission(boolean expression) {
        if (!expression) {
            throwException(HttpStatus.FORBIDDEN, "sp.common.403");
        }
    }

    /**
     * 403，已登录，但是仍然没有权限
     * 用于越权检查，资源归属
     *
     * @param expression
     */
    public static void hasPermission(boolean expression, String code, Object... args) {
        if (!expression) {
            throwException(HttpStatus.FORBIDDEN, code, args);
        }
    }

    public static void error(boolean expression, String code, Object... args) {
        error(expression, HttpStatus.PRECONDITION_FAILED, code, args);
    }

    /**
     * 用于其他系统错误
     * 因为
     * 需要传入具体错误码和错误信息
     *
     * @param expression
     * @param statusCode
     * @param code
     * @param args
     */
    public static void error(boolean expression, HttpStatus statusCode, String code, Object... args) {
        if (!expression) {
            throwException(statusCode.value(), code, args);
        }
    }

    /**
     * 参数校验类的异常，不考虑业务
     *
     * @param code
     * @param args
     */
    public static void throwParamException(String code, Object... args) {
        throw new CommonException(HttpStatus.BAD_REQUEST, code, args);
    }

    /**
     * 业务校验异常，
     * 例如只允许买几个这种定制化的需求或功能
     *
     * @param code
     * @param args
     */
    public static void throwBizException(String code, Object... args) {
        throw new CommonException(HttpStatus.PRECONDITION_FAILED, code, args);
    }

    /**
     * 自己想抛啥，抛啥
     * @param statusCode
     * @param code
     * @param args
     */
    public static void throwException(HttpStatus statusCode, String code, Object... args) {
        throwException(statusCode.value(), code, args);
    }

    public static void throwException(int statusCode, String code, Object... args) {
        throw new CommonException(statusCode, code, args);
    }
}

