package com.cs.sp.enums;

import lombok.Getter;

/**
 * @author sb
 * @date 2024/7/30 02:48
 */
@Getter
public enum ShowedEnum {
    /**
     * 是否显示
     */
    SHOW((byte)1,"显示"),
    HIDE((byte)0,"不显示"),
    ;

    private Byte code;
    private String msg;

    ShowedEnum(Byte code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public static ShowedEnum of(Byte code){
        for(ShowedEnum value: values()){
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
