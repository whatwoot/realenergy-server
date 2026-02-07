package com.cs.copy.member.api.enums;

import lombok.Getter;

/**
 * @author fiona
 * @date 2024/11/2 01:05
 */
@Getter
public enum LoginTypeEnum {
    /**
     * tg登录和钱包登录
     */
    MAIL((byte)1, "email"),
    SMS((byte)2, "mobile"),
    BSC((byte)3, "bsc"),
    OTP((byte)8, "OTP"),
    PIN((byte)9, "pin"),
    ;

    private Byte code;
    private String msg;

    LoginTypeEnum(Byte code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public static LoginTypeEnum of(Byte code){
        for(LoginTypeEnum value: values()){
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
