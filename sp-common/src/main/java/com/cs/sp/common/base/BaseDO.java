package com.cs.sp.common.base;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * @author sb
 * @date 2022/3/31 02:31
 */
public class BaseDO implements Serializable {

    private static final long serialVersionUID = -5337300651210378923L;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
