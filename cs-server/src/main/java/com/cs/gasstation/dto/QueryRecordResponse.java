package com.cs.gasstation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * GasStation查询订单记录响应参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryRecordResponse {
    /**
     * 订单ID
     */
    @JsonProperty("trade_no")
    private String tradeNo;

    @JsonProperty("id")
    private Integer id;

    /**
     * 业务方ID
     */
    @JsonProperty("request_id")
    private String requestId;

    /**
     * 状态: 0创建订单成功 1代理资源成功 2代理资源失败 3部分成功 10回收成功
     */
    @JsonProperty("status")
    private Integer status;

    /**
     * 资源接收地址
     */
    @JsonProperty("receive_address")
    private String receiveAddress;

    /**
     * 支付金额
     */
    private BigDecimal amount;

    /**
     * 能量数量
     */
    @JsonProperty("energy_num")
    private Integer energyNum;

    /**
     * 已代理能量数量
     */
    @JsonProperty("delegate_energy_num")
    private Integer delegateEnergyNum;

    /**
     * 已回收能量数量
     */
    @JsonProperty("reclaim_energy_num")
    private Integer reclaimEnergyNum;

    /**
     * 带宽数量
     */
    @JsonProperty("net_num")
    private Integer netNum;

    /**
     * 已代理带宽数量
     */
    @JsonProperty("delegate_net_num")
    private Integer delegateNetNum;

    /**
     * 已回收带宽数量
     */
    @JsonProperty("reclaim_net_num")
    private Integer reclaimNetNum;

    /**
     * 能量子订单列表
     */
    @JsonProperty("item_list")
    private List<EnergyItemInfo> itemList;

    /**
     * 带宽子订单列表
     */
    @JsonProperty("net_item_list")
    private List<NetItemInfo> netItemList;
}
