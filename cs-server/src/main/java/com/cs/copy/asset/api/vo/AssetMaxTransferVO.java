package com.cs.copy.asset.api.vo;

import com.cs.sp.common.base.BaseVO;
import com.cs.sp.serializer.MoneySerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @authro fun
 * @date 2026/1/3 00:26
 */
@Data
@Schema(description = "最大可移除押金")
public class AssetMaxTransferVO extends BaseVO {
    @Schema(description = "代币")
    private String symbol;
    @Schema(description = "余额")
    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal balance;
    @Schema(description = "冻结")
    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal frozen;

    @Schema(description = "可提/转出")
    @JsonSerialize(using = MoneySerializer.class)
    public BigDecimal getCanWithdraw(){
        BigDecimal left = balance.subtract(frozen);
        return left.compareTo(BigDecimal.ZERO) > 0 ? left : BigDecimal.ZERO;
    }
}
