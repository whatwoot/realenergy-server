package com.cs.energy.thd.api.enums;

import lombok.Getter;

/**
 * @authro fun
 * @date 2025/11/23 23:36
 */
@Getter
public enum MerchantPaymentTypeEnum {
    /**
     *
     */
    WX((byte)1,"wx"),
    ALIPAY((byte)2,"alipay"),
    UNIONPAY((byte)3,"unionpay"),
    UNION((byte)4,"union"),
    ;

    private Byte code;
    private String msg;

    MerchantPaymentTypeEnum(Byte code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public static MerchantPaymentTypeEnum of(Byte code){
        for(MerchantPaymentTypeEnum value: values()){
            if(value.eq(code)){
                return value;
            }
        }
        return null;
    }

    public static Byte ofMsg(String type) {
        for (MerchantPaymentTypeEnum value : values()) {
            if(value.msg.equals(type)){
                return value.code;
            }
        }
        return null;
    }

    public boolean eq(Byte code){
        return this.getCode().equals(code);
    }
}
