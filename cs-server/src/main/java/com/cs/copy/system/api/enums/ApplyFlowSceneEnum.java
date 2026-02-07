package com.cs.copy.system.api.enums;

import lombok.Getter;

/**
 * @authro fun
 * @date 2025/10/9 16:53
 */
@Getter
public enum ApplyFlowSceneEnum {
    NFT_SELL("0101","NFT卖出审核"),
    ;

    private String code;
    private String msg;

    ApplyFlowSceneEnum(String code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public static ApplyFlowSceneEnum of(String code){
        for(ApplyFlowSceneEnum value: values()){
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
