package com.cs.energy.evm.api.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.cs.sp.common.base.BaseVO;
import com.cs.sp.serializer.MoneySerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author fiona
 * @date 2025/3/1 00:57
 */
@Data
@Schema(description = "法币汇率响应")
public class SymbolPriceWeb2VO extends BaseVO {
    @Schema(description = "交易对")
    private String symbol;

    @Schema(description = "基准货币")
    private String baseCurrency;

    @Schema(description = "计价货币")
    private String quoteCurrency;

    @Schema(description = "当前价格")
    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal price;
    @Schema(description = "价格精度")
    private Integer priceDecimals;

    @Schema(description = "1分钟均价")
    @TableField("avg_price_1m")
    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal avgPrice1m;

    @Schema(description = "更新于")
    private Long updateAt;
}
