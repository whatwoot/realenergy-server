package com.cs.oksdk.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * @authro fun
 * @date 2025/11/30 16:43
 */
@Getter
public enum PosMode {

    /**
     */
    LONG_SHORT("long_short_mode"),
    /**
     */
    NET("net_mode");

    private final String value;

    PosMode(final String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }

    public static PosMode of(final String value) {
        for (PosMode mode : values()) {
            if (mode.value.equals(value)) {
                return mode;
            }
        }
        return null;
    }

    public boolean eq(final String mode) {
        return this.value.equals(mode);
    }
}
