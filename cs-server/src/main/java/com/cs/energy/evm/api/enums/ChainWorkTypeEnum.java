package com.cs.energy.evm.api.enums;

import lombok.Getter;

/**
 * @author fiona
 * @date 2024/6/3 22:30
 */
@Getter
public enum ChainWorkTypeEnum {
    /**
     * 链上事务
     */
    DEPOSIT((byte)1, "充值"),
    COLLECT((byte)2, "归集"),
    GAS((byte)3, "GAS转账"),
    WITHDRAW((byte)4, "evm提现"),
    BUY_COIN_ADD_LP((byte) 5, "购买代币加池子"),
    ;

    private Byte code;
    private String msg;

    ChainWorkTypeEnum(Byte code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public static ChainWorkTypeEnum of(Byte code){
        for(ChainWorkTypeEnum value: values()){
            if(value.eq(code)){
                return value;
            }
        }
        return null;
    }

    public boolean eq(Byte code){
        return this.getCode().equals(code);
    }
}
