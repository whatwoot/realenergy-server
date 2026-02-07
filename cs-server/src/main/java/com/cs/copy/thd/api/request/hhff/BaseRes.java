package com.cs.copy.thd.api.request.hhff;

import com.alibaba.fastjson2.annotation.JSONField;
import com.cs.sp.common.base.BaseDTO;
import lombok.Data;

/**
 * @authro fun
 * @date 2025/4/1 14:57
 */
@Data
public class BaseRes extends BaseDTO {
    @JSONField(name = "merchant_id")
    private Long merchantId;
    private Long timestamp;
}
