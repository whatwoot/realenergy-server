package com.cs.sp.common;

import lombok.Data;

/**
 * 特殊处理的wrapperException
 * 该异常下的信息，已经被国际化处理
 * @author sb
 * @date 2018/9/7 02:54
 */
@Data
public class WrapperException extends RuntimeException {

    private String fullMsg;

    public WrapperException(Throwable e) {
        super(e);
    }

    public WrapperException(Throwable cause, String fullMsg) {
        super(cause);
        this.fullMsg = fullMsg;
    }

}
