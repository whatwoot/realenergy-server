package com.cs.copy.thd.api.enums;

import lombok.Getter;

/**
 * @authro fun
 * @date 2025/4/12 15:53
 */
@Getter
public enum HhffPayModeEnum {

    WECHAT_PAY((byte)1, "wxpay"),
    ALIPAY((byte)2, "alipay"),
    UNION((byte)4, "unionpay"),
    ;

    private Byte code;
    private String msg;

    HhffPayModeEnum(Byte code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public static HhffPayModeEnum of(Byte code){
        for(HhffPayModeEnum value: values()){
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
