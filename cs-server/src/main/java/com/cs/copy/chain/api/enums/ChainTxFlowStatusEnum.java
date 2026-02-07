package com.cs.copy.chain.api.enums;

import lombok.Getter;

/**
 * @author fiona
 * @date 2024/6/4 02:56
 */
@Getter
public enum ChainTxFlowStatusEnum {
    /**
     *
     */
    NOT_DEAL((byte)0, "待处理"),
    CONFIRMED((byte)1, "已确认"),
    FAIL((byte)2, "确认异常")
    ;


    private Byte code;
    private String msg;

    ChainTxFlowStatusEnum(Byte code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public static ChainTxFlowStatusEnum resolve(Byte code){
        for(ChainTxFlowStatusEnum value: values()){
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
