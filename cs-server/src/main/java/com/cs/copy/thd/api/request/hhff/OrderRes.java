package com.cs.copy.thd.api.request.hhff;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @authro fun
 * @date 2025/4/1 22:41
 */
@Data
public class OrderRes extends BasePay {
    @JSONField(name = "distribute_code")
    private Long distributeCode;
    private BigDecimal amount;
    private String status;
}
