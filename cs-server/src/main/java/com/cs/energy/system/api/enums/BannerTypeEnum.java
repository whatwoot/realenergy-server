package com.cs.energy.system.api.enums;

import lombok.Getter;

/**
 * @authro fun
 * @date 2025/6/23 00:48
 */
@Getter
public enum BannerTypeEnum {

    NORMAL((byte)0, "普通"),
    APP((byte)1, "发现页入口")
    ;

    private Byte code;
    private String msg;

    BannerTypeEnum(Byte code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public static BannerTypeEnum of(Byte code){
        for(BannerTypeEnum value: values()){
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
