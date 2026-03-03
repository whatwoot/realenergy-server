package com.cs.energy.evm.api.enums;

import lombok.Getter;

/**
 * @author fiona
 * @date 2024/6/3 22:30
 */
@Getter
public enum ChainWorkProcessedEnum {
    /**
     * 处理结果
     */
    NOOP((byte) 0, "待处理"),
    OK((byte) 1, "成功"),
    FAIL((byte)2, "失败"),
    ;

    private Byte code;
    private String msg;

    ChainWorkProcessedEnum(Byte code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public static ChainWorkProcessedEnum of(Byte code){
        for(ChainWorkProcessedEnum value: values()){
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
