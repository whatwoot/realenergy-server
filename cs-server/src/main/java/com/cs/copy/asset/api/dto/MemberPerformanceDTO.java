package com.cs.copy.asset.api.dto;

import com.cs.sp.common.base.BaseDTO;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @authro fun
 * @date 2025/11/20 04:02
 */
@Data
public class MemberPerformanceDTO extends BaseDTO {
    private Long uid;
    private BigDecimal smallPerformance;
}
