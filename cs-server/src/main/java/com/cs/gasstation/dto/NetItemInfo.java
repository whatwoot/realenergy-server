package com.cs.gasstation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 带宽子订单信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NetItemInfo {
    /**
     * 带宽代理数量
     */
    @JsonProperty("net_num")
    private Integer netNum;

    /**
     * 带宽价格，单位sun
     */
    @JsonProperty("net_price")
    private String netPrice;

    /**
     * 带宽代理TXID
     */
    @JsonProperty("net_txid")
    private String netTxid;

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
     * 带宽回收TXID
     */
    @JsonProperty("reclaim_txid")
    private String reclaimTxid;

    /**
     * 带宽回收时间
     */
    @JsonProperty("reclaim_time")
    private String reclaimTime;
}
