package com.cs.energy.evm.api.enums;

import lombok.Getter;

/**
 * @authro fun
 * @date 2025/3/21 18:38
 */
@Getter
public enum OrgEnum {

    NORMAL(0, "普通用户"),
    MERCHANT(1,"商家")
    ;

    private Integer code;
    private String msg;

    OrgEnum(Integer code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public static OrgEnum of(Integer code){
        for(OrgEnum value: values()){
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
