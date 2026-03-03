package com.cs.gasstation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * GasStation查询余额请求参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BalanceRequest {
    /**
     * 当前时间戳
     */
    private Long time;
}
