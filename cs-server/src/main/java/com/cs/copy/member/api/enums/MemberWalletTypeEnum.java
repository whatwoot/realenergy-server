package com.cs.copy.member.api.enums;

import lombok.Getter;

/**
 * @author fiona
 * @date 2024/11/2 01:05
 */
@Getter
public enum MemberWalletTypeEnum {
    /**
     * tg登录和钱包登录
     */
    RECHARGE((byte)1, "充值钱包"),
    WITHDRAW((byte)2, "提现钱包"),
    ;

    private Byte code;
    private String msg;

    MemberWalletTypeEnum(Byte code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public static MemberWalletTypeEnum of(Byte code){
        for(MemberWalletTypeEnum value: values()){
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
