package com.cs.energy.evm.api.enums;

import lombok.Getter;

/**
 * @author fiona
 * @date 2024/6/3 22:30
 */
@Getter
public enum ChainWorkCollectedEnum {
    /**
     * 链上事务
     */
    NOOP((byte) 0, "无须处理"),
    OK((byte) 1, "已完成"),
    FAIL((byte)2, "失败"),
    CHECK((byte)3, "待检查"),
    WAIT_COLLECT((byte)4, "待归集"),
    WAIT_GAS((byte)5, "待gas"),
    ;

    private Byte code;
    private String msg;

    ChainWorkCollectedEnum(Byte code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public static ChainWorkCollectedEnum of(Byte code){
        for(ChainWorkCollectedEnum value: values()){
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
