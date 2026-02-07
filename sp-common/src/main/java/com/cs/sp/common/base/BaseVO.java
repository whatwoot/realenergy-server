package com.cs.sp.common.base;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * 基类
 * @author sb
 * @date 2022/3/31 02:31
 */
public class BaseVO implements Serializable {

    private static final long serialVersionUID = -4904831612818461696L;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
