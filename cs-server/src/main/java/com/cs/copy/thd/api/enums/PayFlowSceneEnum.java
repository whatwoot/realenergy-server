package com.cs.copy.thd.api.enums;

import lombok.Getter;

/**
 * @authro fun
 * @date 2025/3/21 05:24
 */
@Getter
public enum PayFlowSceneEnum {
    /**
     *
     */
    OCTOPUS_PAY("001", "CNY提现支付"),
    HHFF_PAY("002", "HHFF提现支付");

    private String code;
    private String msg;

    PayFlowSceneEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static PayFlowSceneEnum of(String code) {
        for (PayFlowSceneEnum value : values()) {
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
