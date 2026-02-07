package com.cs.sp.enums;

import lombok.Getter;

/**
 * @author sb
 * @date 2024/7/30 02:48
 */
@Getter
public enum StatusEnum {
    /**
     * 是否显示
     */
    ENABLE((byte)1,"正常"),
    DISABLED((byte)0,"禁用"),
    ;

    private Byte code;
    private String msg;

    StatusEnum(Byte code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public static StatusEnum of(Byte code){
        for(StatusEnum value: values()){
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
