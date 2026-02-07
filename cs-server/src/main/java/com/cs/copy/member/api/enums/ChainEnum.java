package com.cs.copy.member.api.enums;

import lombok.Getter;

/**
 * @author fiona
 * @date 2024/12/10 02:22
 */
@Getter
public enum ChainEnum {
    /**
     * 链
     */
    BSC("bsc", "BSC链"),
    CNY("cny","人民币")
    ;

    private String code;
    private String msg;

    ChainEnum(String code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public static ChainEnum of(String code){
        for(ChainEnum value: values()){
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
