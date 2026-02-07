package com.cs.copy.thd.api.enums;

import lombok.Getter;

/**
 * @authro fun
 * @date 2025/3/21 05:04
 */
@Getter
public enum PayFlowStatusEnum {

    FAIL((byte)0, "支付失败"),
    OK((byte)1, "支付成功"),
    START((byte)2, "支付中"),
    WAIT_CALLBACK((byte)3, "待回调"),
    CHECK((byte)5, "待复查"),
    CONFLICT((byte)6, "有冲突"),
    OTHER((byte)7, "其他异常")
    ;

    private Byte code;
    private String msg;

    PayFlowStatusEnum(Byte code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public static PayFlowStatusEnum of(Byte code){
        for(PayFlowStatusEnum value: values()){
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
