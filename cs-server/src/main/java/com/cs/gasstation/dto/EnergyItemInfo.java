package com.cs.gasstation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 能量子订单信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnergyItemInfo {
    /**
     * 能量代理数量
     */
    @JsonProperty("energy_num")
    private Integer energyNum;

    /**
     * 能量价格，单位sun
     */
    @JsonProperty("energy_price")
    private String energyPrice;

    /**
     * 能量代理TXID
     */
    @JsonProperty("energy_txid")
    private String energyTxid;

    /**
     * 代理时间
     */
    @JsonProperty("delegate_time")
    private String delegateTime;

    /**
     * 发送资源地址
     */
    @JsonProperty("resource_address")
    private String resourceAddress;

    /**
     * 能量回收TXID
     */
    @JsonProperty("reclaim_txid")
    private String reclaimTxid;

    /**
     * 能量回收时间
     */
    @JsonProperty("reclaim_time")
    private String reclaimTime;
}
