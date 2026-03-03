package com.cs.energy.asset.api.enums;

import lombok.Getter;

/**
 * @author fiona
 * @date 2025/2/25 20:21
 */
@Getter
public enum AssetTypeEnum {
    /**
     *
     */
    DEFAULT((byte)0, "默认"),
    COPY((byte)1, "跟单账户")
    ;

    private Byte code;
    private String msg;

    AssetTypeEnum(Byte code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public static AssetTypeEnum of(Byte code){
        for(AssetTypeEnum value: values()){
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
