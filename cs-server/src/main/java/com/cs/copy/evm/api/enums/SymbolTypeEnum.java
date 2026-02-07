package com.cs.copy.evm.api.enums;

import lombok.Getter;

/**
 * @authro fun
 * @date 2025/10/3 01:51
 */
@Getter
public enum SymbolTypeEnum {

    WITHDRAWAL((byte)0, "withdrawal"),
    TOPUP((byte)1, "topup"),
    ;

    private Byte code;
    private String msg;

    SymbolTypeEnum(Byte code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public static SymbolTypeEnum of(Byte code){
        for(SymbolTypeEnum value: values()){
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
