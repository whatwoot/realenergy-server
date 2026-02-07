package com.cs.oksdk.enums;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;


@Getter
public enum InstrumentType {

    /**
     * 币币
     */
    @JSONField(name = "SPOT")
    SPOT("SPOT"),
    /**
     * 币币杠杆
     */
    @JSONField(name = "MARGIN")
    MARGIN("MARGIN"),
    /**
     * 永续合约
     */
    @JSONField(name = "SWAP")
    SWAP("SWAP"),
    /**
     * 交割合约
     */
    @JSONField(name = "FUTURES")
    FUTURES("FUTURES"),
    /**
     * 期权
     */
    @JSONField(name = "OPTION")
    OPTION("OPTION"),

    /**
     * 全部
     */
    @JSONField(name = "ANY")
    ANY("ANY");

    private final String value;

    InstrumentType(final String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }

}
