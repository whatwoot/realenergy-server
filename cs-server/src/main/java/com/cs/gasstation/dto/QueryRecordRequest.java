package com.cs.gasstation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * GasStation查询订单记录请求参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryRecordRequest {
    /**
     * 多个request_id的字符串，英文逗号分割
     */
    @JsonProperty("request_ids")
    private String requestIds;
}
