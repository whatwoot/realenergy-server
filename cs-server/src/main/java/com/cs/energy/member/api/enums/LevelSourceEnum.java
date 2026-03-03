package com.cs.energy.member.api.enums;

import lombok.Getter;

/**
 * @author fiona
 * @date 2024/12/11 03:13
 */
@Getter
public enum LevelSourceEnum {
    /**
     * 奖励来源
     */
    PARENT((byte)0,"上级"),
    ORIGIN((byte)1, "源奖励"),
    ;

    private Byte code;
    private String msg;

    LevelSourceEnum(Byte code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public static LevelSourceEnum of(Byte code){
        for(LevelSourceEnum value: values()){
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
