package com.cs.energy.system.api.enums;

import lombok.Getter;

/**
 * @authro fun
 * @date 2025/6/23 00:50
 */
@Getter
public enum BannerPosEnum {
    H5_INDEX("index.banner","h5首页"),
    APP_INDEX("app.index.banner","app首页"),
    APP_FINDER("app.find.banner","app发现"),
    ;

    private String code;
    private String msg;

    BannerPosEnum(String code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public static BannerPosEnum of(String code){
        for(BannerPosEnum value: values()){
            if(value.eq(code)){
                return value;
            }
        }
        return null;
    }

    public boolean eq(String code){
        return this.getCode().equals(code);
    }
}
