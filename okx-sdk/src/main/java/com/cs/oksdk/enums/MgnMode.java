package com.cs.oksdk.enums;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum MgnMode {

    /**
     * 全仓
     */
    @JSONField(name = "cross")
    CROSS("cross"),
    /**
     * 逐仓
     */
    @JSONField(name = "isolated")
    ISOLATED("isolated");

    private final String value;

    MgnMode(final String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }

    public static MgnMode of(final String value) {
        for (MgnMode mode : values()) {
            if (mode.value.equals(value)) {
                return mode;
            }
        }
        return null;
    }
}
