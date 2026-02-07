package com.cs.copy.asset.api.enums;

import lombok.Getter;

/**
 * @author fiona
 * @date 2024/12/9 16:43
 */
@Getter
public enum WithdrawChainEnum {
    /**
     * 提现的链
     */
    TON("ton", "ton链"),
    BSC("bsc", "bsc链"),
    ;

    private String code;
    private String msg;

    WithdrawChainEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static WithdrawChainEnum of(String code) {
        for (WithdrawChainEnum value : values()) {
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
