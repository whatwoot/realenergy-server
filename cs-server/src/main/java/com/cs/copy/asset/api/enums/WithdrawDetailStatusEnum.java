package com.cs.copy.asset.api.enums;

import lombok.Getter;

/**
 * @author fiona
 * @date 2024/10/3 17:32
 */
@Getter
public enum WithdrawDetailStatusEnum {
    /**
     * 状态。0=已提交，1=已完成，2=发送中，3=确认中，4=转账失败
     */
    COMMIT((byte)0, "待发送"),
    DONE((byte)1, "已完成"),
    SENDING((byte)2, "发送中"),
    CONFIRMING((byte)3, "待确认"),
    FAILED((byte)4, "转账失败"),
    RESEND((byte)5, "重新发送"),
    ;

    private Byte code;
    private String msg;

    WithdrawDetailStatusEnum(Byte code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public static WithdrawDetailStatusEnum of(Byte code){
        for(WithdrawDetailStatusEnum value: values()){
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
