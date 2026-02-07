package com.cs.copy.system.api.enums;

import lombok.Getter;

/**
 * @authro fun
 * @date 2025/5/1 00:28
 */
@Getter
public enum ConfigTypeEnum {

    CONFIG((byte)0, "配置类"),
    DATA((byte)1, "数据类"),
    ;

    private Byte code;
    private String msg;

    ConfigTypeEnum(Byte code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public static ConfigTypeEnum of(Byte code){
        for(ConfigTypeEnum value: values()){
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
