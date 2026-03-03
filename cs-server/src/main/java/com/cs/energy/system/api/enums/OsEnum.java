package com.cs.energy.system.api.enums;

import lombok.Getter;

/**
 * @authro fun
 * @date 2025/6/23 14:08
 */
@Getter
public enum OsEnum {
    IOS("ios","iso"),
    ANDROID("android","android"),
    H5("h5","h5"),
    ;

    private String code;
    private String msg;

    OsEnum(String code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public static OsEnum of(String code){
        for(OsEnum value: values()){
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
