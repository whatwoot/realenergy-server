package com.cs.gasstation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * GasStation创建购买资源订单请求参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    /**
     * 业务方ID
     */
    @JsonProperty("request_id")
    private String requestId;

    /**
     * 资源接收地址
     */
    @JsonProperty("receive_address")
    private String receiveAddress;

    /**
     * 购买资源方式
     * 0: 客户指定数量(默认)
     * 1: 系统预估
     */
    @JsonProperty("buy_type")
    private Integer buyType;

    /**
     * 租赁周期code
     * 示例：10分钟:10010, 1小时:20001, 1天:30001
     */
    @JsonProperty("service_charge_type")
    private String serviceChargeType;

    /**
     * 购买能量数量，最小值64000
     * 选择buy_type=0时需要填写
     */
    @JsonProperty("energy_num")
    private Integer energyNum;

    /**
     * 购买带宽数量，最小值5000
     * 选择buy_type=0时需要填写
     */
    @JsonProperty("net_num")
    private Integer netNum;

    /**
     * 转账到账地址，用于预估矿工费
     * 选择buy_type=1时需要填写
     */
    @JsonProperty("address_to")
    private String addressTo;

    /**
     * 合约地址，用于预估矿工费
     * 选择buy_type=1时需要填写
     */
    @JsonProperty("contract_address")
    private String contractAddress;
}
