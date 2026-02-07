package com.cs.copy.asset.api.dto;

import com.cs.sp.common.base.BaseDTO;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author fiona
 * @date 2024/11/20 04:19
 */
@Data
public class SummaryDTO extends BaseDTO {
    private BigDecimal dayBalance;
    private BigDecimal dayFrozen;
    private BigDecimal totalBalance;
    private BigDecimal totalFrozen;
}
