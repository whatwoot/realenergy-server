package com.cs.energy.thd.api.request.hhff;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

/**
 * @authro fun
 * @date 2025/4/1 14:56
 */
@Data
public class PayQuery extends BasePay {
    @JSONField(name = "distribute_code")
    private Long distributeCode;
}
