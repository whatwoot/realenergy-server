package com.cs.copy.member.api.enums;

import lombok.Getter;

@Getter
public enum CurrencyEnum {
    /**
     *
     */
    CNY("CNY", "人民币"),
    USD("USD", "美元"),
    THB("THB", "泰株"),
    KRW("KRW", "韩元"),
    VND("VND", "越南盾"),
    ;

    private String code;
    private String msg;

    CurrencyEnum(String code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public static CurrencyEnum of(String code){
        for(CurrencyEnum value: values()){
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
