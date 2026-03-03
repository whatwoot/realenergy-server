package com.cs.energy.system.api.enums;

import lombok.Getter;

/**
 * @author fiona
 * @date 2025/2/18 18:46
 */
@Getter
public enum SystemStatusEnum {
    /**
     *
     */
    OK("1", "正常"),
    MAINTENANCE("2", "准备维护"),
    STOP("0", "停止"),
    ;

    private String code;
    private String msg;

    SystemStatusEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static SystemStatusEnum of(String code) {
        for (SystemStatusEnum value : values()) {
            if (value.eq(code)) {
                return value;
            }
        }
        return null;
    }

    public boolean eq(String code) {
        return this.getCode().equals(code);
    }
}
