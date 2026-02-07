package com.cs.sp.enums;

import lombok.Getter;

/**
 * @author sb
 * @date 2024/8/3 11:40
 */
@Getter
public enum YesNoStrEnum {
    /**
     * 是否
     */
    YES("1", "是"),
    NO("0","否");
    ;

    private String code;
    private String msg;

    YesNoStrEnum(String code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public static YesNoStrEnum of(String code){
        for(YesNoStrEnum value: values()){
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
