package com.cs.oksdk.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum Side {

    /**
     * 买
     */
    BUY("buy"),
    /**
     * 卖
     */
    SELL("sell");

    private final String value;

    Side(final String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }

    public static Side of(String v) {
        for (Side ins : values()) {
            if(ins.value.equals(v)) {
                return ins;
            }
        }
        return null;
    }

    public boolean eq(String side) {
        return this.value.equals(side);
    }
}
