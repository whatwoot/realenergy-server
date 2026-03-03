package com.cs.energy.system.api.dto;

import com.cs.sp.common.base.BaseDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author fiona
 * @date 2024/3/11 03:04
 */
@Data
public class FixBuyCoinDTO extends BaseDTO {
    // 要买的币种
    private String symbol;
    // 总金额
    private BigDecimal totalAmount;
    private List<Long> idLis = new ArrayList<>();
    private List<BigDecimal> amountList = new ArrayList<>();
}
