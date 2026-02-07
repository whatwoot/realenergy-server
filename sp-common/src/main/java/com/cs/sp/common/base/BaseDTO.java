package com.cs.sp.common.base;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * 基类
 * @author sb
 * @date 2022/3/31 02:31
 */
public class BaseDTO implements Serializable {

    private static final long serialVersionUID = 8756092774025093269L;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
