package com.cs.copy.evm.api.enums;

import lombok.Getter;

/**
 * @author fiona
 * @date 2024/6/3 22:30
 */
@Getter
public enum ChainWorkTxStatusEnum {
    /**
     * 链上事务
     */
    WAIT((byte) 0, "待处理"),
    OK((byte) 1, "已完成"),
    CONFIRMING((byte) 2, "待链上确认"),
    FAIL((byte)3, "失败"),
    DOING((byte)4, "转账中")
    ;

    private Byte code;
    private String msg;

    ChainWorkTxStatusEnum(Byte code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public static ChainWorkTxStatusEnum of(Byte code){
        for(ChainWorkTxStatusEnum value: values()){
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
