package com.cs.copy.thd.api.enums;

import lombok.Getter;

/**
 * @authro fun
 * @date 2025/3/21 05:04
 */
@Getter
public enum PayFlowPayStatusEnum {

    NOT_PAY((byte)0, "未付"),
    PAYED((byte)1, "已付"),
    FALLBACK((byte)2, "退款"),
    ;

    private Byte code;
    private String msg;

    PayFlowPayStatusEnum(Byte code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public static PayFlowPayStatusEnum of(Byte code){
        for(PayFlowPayStatusEnum value: values()){
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
