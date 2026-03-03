package com.cs.gasstation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * GasStation创建订单响应参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderResponse {
    /**
     * 订单ID
     */
    @JsonProperty("tradeNo")
    private String tradeNo;

    @JsonProperty("trade_no")
    private String trade2No;
}
