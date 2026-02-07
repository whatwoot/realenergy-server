package com.cs.copy.asset.api.enums;

import lombok.Getter;

/**
 * @authro fun
 * @date 2026/1/6 05:00
 */
@Getter
public enum CheckRateSceneEnum {
    TRANSFER("transfer","转账后检查"),
    SETTLE("settle", "结算后检查"),
    API_ORDER("apiOrder", "api下单检查"),
    SYS("sys", "手动检查"),
    STOP_LOSS("stopLoss", "手动止损")
    ;

    private String code;
    private String msg;

    CheckRateSceneEnum(String code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public static CheckRateSceneEnum of(String code){
        for(CheckRateSceneEnum value: values()){
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
