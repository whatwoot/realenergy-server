package com.cs.energy.chain.api.enums;

import lombok.Getter;

/**
 * @author fiona
 * @date 2024/11/23 16:54
 */
@Getter
public enum AddressTypeEnum {
    /**
     *
     */
    RECHARGE((byte)1, "充值地址"),
    COLLECT((byte)2, "归集地址"),
    FEE((byte)3, "手续费地址"),
    WITHDRAW((byte)4, "提现地址"),
    ADD_LP((byte)5, "加LP钱包"),
    ;

    private Byte code;
    private String msg;

    AddressTypeEnum(Byte code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public static AddressTypeEnum of(Byte code){
        for(AddressTypeEnum value: values()){
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
