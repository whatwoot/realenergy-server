package com.cs.sp.common;

import com.cs.sp.common.exception.BaseException;

/**
 * 定制statusCode的异常
 * 主要用于业务逻辑的异常
 * @see WebAssert@throwException
 * @author sb
 * @date 2018/9/7 02:54
 */
public class CommonException extends BaseException {

    public CommonException(HttpStatus status, String code, Object... args) {
        this(status.value(), code, args);
    }

    public CommonException(int statusCode, String code, Object... args) {
        super(statusCode, code, args);
    }
}
