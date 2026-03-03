package com.cs.energy.thd.api.enums;

import lombok.Getter;

/**
 * @authro fun
 * @date 2025/4/12 15:53
 */
@Getter
public enum OctopusPayModeEnum {

    WECHAT_PAY((byte)1, "wxpay"),
    ALIPAY((byte)2, "alipay"),
    ;

    private Byte code;
    private String msg;

    OctopusPayModeEnum(Byte code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public static OctopusPayModeEnum of(Byte code){
        for(OctopusPayModeEnum value: values()){
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
