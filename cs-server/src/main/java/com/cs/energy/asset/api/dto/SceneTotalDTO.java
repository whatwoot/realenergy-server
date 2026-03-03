package com.cs.energy.asset.api.dto;

import com.cs.sp.common.base.BaseDTO;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @authro fun
 * @date 2025/11/23 23:26
 */
@Data
public class SceneTotalDTO extends BaseDTO {
    private String symbol;
    private String scene;
    private BigDecimal amount;
}
