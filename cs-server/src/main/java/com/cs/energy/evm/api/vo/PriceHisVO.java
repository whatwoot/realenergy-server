package com.cs.energy.evm.api.vo;

import com.cs.sp.common.base.BaseVO;
import com.cs.sp.serializer.MoneySerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author fiona
 * @date 2025/3/1 00:57
 */
@Data
@Schema(description = "平台币价格")
public class PriceHisVO extends BaseVO {
    @Schema(description = "token0价格")
    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal token0Price;

    @Schema(description = "当前价格")
    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal token1Price;

}
