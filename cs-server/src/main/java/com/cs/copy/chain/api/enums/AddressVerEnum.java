package com.cs.copy.chain.api.enums;

import lombok.Getter;

/**
 * @author fiona
 * @date 2024/11/23 16:54
 */
@Getter
public enum AddressVerEnum {
    /**
     * 钱包版本
     */
    V5R1("v5r1", "v5r1"),
    V5("v5", "v5"),
    ;

    private String code;
    private String msg;

    AddressVerEnum(String code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public static AddressVerEnum of(String code){
        for(AddressVerEnum value: values()){
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
