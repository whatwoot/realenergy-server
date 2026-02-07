package com.cs.sp.common.base;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * @author sb
 * @date 2024/7/29 15:45
 */
public class BaseRequest implements Serializable {
    private static final long serialVersionUID = 1265217899064053789L;
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
