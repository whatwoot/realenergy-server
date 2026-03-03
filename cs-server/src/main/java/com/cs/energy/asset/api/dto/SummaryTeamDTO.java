package com.cs.energy.asset.api.dto;

import com.cs.sp.common.base.BaseDTO;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author fiona
 * @date 2024/11/20 04:19
 */
@Data
public class SummaryTeamDTO extends BaseDTO {
    private BigDecimal myBalance;
    private BigDecimal myFrozen;
    private BigDecimal totalBalance;
    private BigDecimal totalFrozen;
}
