package com.cs.energy.system.api.enums;

import lombok.Getter;

/**
 * @authro fun
 * @date 2025/10/9 16:53
 */
@Getter
public enum ApplyFlowStatusEnum {
    NOT_AUDIT((byte)0, "未审核"),
    AUDITED((byte)1,"已审核"),
    REJECT((byte)2,"已拒绝"),
    IGNORE((byte)3, "已忽略")
    ;

    private Byte code;
    private String msg;

    ApplyFlowStatusEnum(Byte code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public static ApplyFlowStatusEnum of(Byte code){
        for(ApplyFlowStatusEnum value: values()){
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
