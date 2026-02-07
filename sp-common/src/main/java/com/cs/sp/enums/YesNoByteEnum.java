package com.cs.sp.enums;

import lombok.Getter;

/**
 * @author sb
 * @date 2024/8/3 11:40
 */
@Getter
public enum YesNoByteEnum {
    /**
     * 是否
     */
    YES((byte)1, "是"),
    NO((byte)0, "否"),
    ;

    private Byte code;
    private String msg;

    YesNoByteEnum(Byte code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public static YesNoByteEnum of(Byte code){
        for(YesNoByteEnum value: values()){
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
