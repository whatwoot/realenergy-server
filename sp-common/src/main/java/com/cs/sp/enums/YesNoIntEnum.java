package com.cs.sp.enums;

import lombok.Getter;

/**
 * @author sb
 * @date 2024/8/3 11:40
 */
@Getter
public enum YesNoIntEnum {
    /**
     * 是否
     */
    YES(1, "是"),
    NO(0, "否"),
    ;

    private Integer code;
    private String msg;

    YesNoIntEnum(Integer code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public static YesNoIntEnum of(Integer code){
        for(YesNoIntEnum value: values()){
            if(value.eq(code)){
                return value;
            }
        }
        return null;
    }

    public boolean eq(Integer code){
        return this.getCode().equals(code);
    }
}
