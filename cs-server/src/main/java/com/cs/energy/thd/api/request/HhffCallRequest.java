package com.cs.energy.thd.api.request;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.cs.energy.thd.api.request.hhff.BasePay;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * {"distribute_code":"216559934760813224",
 *         "amount":"140.0","merchant_id":100002,
 *         "status":"dist_success",
 *         "notify":"fail","sign":"73E837E6775D915F916A8313F900B9B6"}
 * @authro fun
 * @date 2025/3/21 01:17
 */
@Data
@Schema(description = "Hhff通知接口")
public class HhffCallRequest extends BasePay {
    private BigDecimal amount;
    @JsonAlias("distribute_code")
    @JSONField(name = "distribute_code")
    private Long distributeCode;
    private String status;
    private String notify;
}


