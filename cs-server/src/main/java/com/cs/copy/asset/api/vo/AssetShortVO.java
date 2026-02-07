package com.cs.copy.asset.api.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.cs.sp.common.base.BaseVO;
import com.cs.sp.serializer.MoneySerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @authro fun
 * @date 2025/10/9 14:18
 */
@Data
public class AssetShortVO extends BaseVO {
    @Schema(description = "代币")
    private String symbol;
    @Schema(description = "余额")
    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal balance;
    @Schema(description = "冻结")
    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal frozen;
    @Schema(description = "价格")
    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal price;

    @Schema(description = "价值")
    @JsonSerialize(using = MoneySerializer.class)
    public BigDecimal getAmount(){
        return balance.multiply(price);
    }
}
