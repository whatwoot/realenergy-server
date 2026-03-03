package com.cs.energy.system.api.enums;

import lombok.Getter;

/**
 * @author fiona
 * @date 2025/2/25 00:32
 */
@Getter
public enum SpImageServiceName {
    /**
     *
     */
    IMAGE_AVATAR("image.avatar","头像上传"),
    ;

    private String code;
    private String msg;

    SpImageServiceName(String code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public static SpImageServiceName of(String code){
        for(SpImageServiceName value: values()){
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
