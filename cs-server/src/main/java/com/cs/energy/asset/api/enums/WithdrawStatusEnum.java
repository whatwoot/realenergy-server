package com.cs.energy.asset.api.enums;

import lombok.Getter;

/**
 * @author fiona
 * @date 2024/10/3 17:32
 */
@Getter
public enum WithdrawStatusEnum {
    /**
     * 状态。0=已提交，1=已完成，2=确认中
     */
    COMMIT((byte)0, "已提交"),
    DONE((byte)1, "已完成"),
    CONFIRMING((byte)2, "进行中"),
    REFUND((byte)3, "已退款"),
    TRANSFERING((byte)4,"转账中"),
    AUDITED((byte)5, "已审核")
    ;

    private Byte code;
    private String msg;

    WithdrawStatusEnum(Byte code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public static WithdrawStatusEnum of(Byte code){
        for(WithdrawStatusEnum value: values()){
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
