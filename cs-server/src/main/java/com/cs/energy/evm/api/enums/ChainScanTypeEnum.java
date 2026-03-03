package com.cs.energy.evm.api.enums;

import lombok.Getter;

/**
 * @author fiona
 * @date 2024/6/3 22:30
 */
@Getter
public enum ChainScanTypeEnum {
    /**
     *
     */
    CONTRACT((byte) 1, "扫合约"),
    NFT((byte) 2, "扫NFT"),
    TOKEN((byte) 3, "扫代币"),
    ADDRESS((byte) 4, "扫地址"),
    TRANSFER((byte) 5, "扫转账"),
    TX((byte) 6, "扫指定事务");

    private Byte code;
    private String msg;

    ChainScanTypeEnum(Byte code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static ChainScanTypeEnum of(Byte code) {
        for (ChainScanTypeEnum value : values()) {
            if (value.eq(code)) {
                return value;
            }
        }
        return null;
    }

    public boolean eq(Byte code){
        return this.getCode().equals(code);
    }
}
