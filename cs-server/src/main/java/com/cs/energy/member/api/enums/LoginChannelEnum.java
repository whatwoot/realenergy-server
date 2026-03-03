package com.cs.energy.member.api.enums;

import lombok.Getter;

/**
 * @author fiona
 * @date 2024/11/2 01:05
 */
@Getter
public enum LoginChannelEnum {
    /**
     * tg登录和钱包登录
     */
    TG((byte)0, "tg"),
    TON_WALLET((byte)1, "wallet"),
    EVM_WALLET((byte)2, "evm"),
    ;

    private Byte code;
    private String msg;

    LoginChannelEnum(Byte code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public static LoginChannelEnum of(Byte code){
        for(LoginChannelEnum value: values()){
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
