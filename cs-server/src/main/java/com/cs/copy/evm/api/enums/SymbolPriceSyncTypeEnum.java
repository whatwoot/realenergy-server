package com.cs.copy.evm.api.enums;

import lombok.Getter;

/**
 * @author fiona
 * @date 2024/9/9 12:14
 */
@Getter
public enum SymbolPriceSyncTypeEnum {
    /**
     * 价格同步方式。0=不同步,1=oracel,2=swap,3=bn,
     */
    NONE((byte) 0, "不同步"),
    ORACLE((byte) 1, "预言机"),
    DEX((byte) 2, "去中心化swap"),
    CEX_BN((byte) 3, "中心化-币安"),
    ;

    private Byte code;
    private String msg;

    SymbolPriceSyncTypeEnum(Byte code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static SymbolPriceSyncTypeEnum of(Byte code) {
        for (SymbolPriceSyncTypeEnum value : values()) {
            if (value.eq(code)) {
                return value;
            }
        }
        return null;
    }

    public boolean eq(Byte code) {
        return this.getCode().equals(code);
    }
}
