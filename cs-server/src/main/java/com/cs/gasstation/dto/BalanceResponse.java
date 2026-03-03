package com.cs.gasstation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * GasStation查询余额响应参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BalanceResponse {
    /**
     * 币种
     */
    private String symbol;

    /**
     * 余额
     */
    private BigDecimal balance;

    /**
     * 充值地址
     */
    @JsonProperty("deposit_address")
    private String depositAddress;
}
