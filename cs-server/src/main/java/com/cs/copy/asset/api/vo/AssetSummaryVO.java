package com.cs.copy.asset.api.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.cs.sp.common.base.BaseVO;
import com.cs.sp.serializer.Money2DecimalsSerializer;
import com.cs.sp.serializer.MoneySerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author fiona
 * @date 2024/9/29 07:28
 */
@Data
@Schema(description = "资产")
public class AssetSummaryVO extends BaseVO {
    private Long uid;
    @Schema(description = "昨日收入")
    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal yesterdayIncome;

    @Schema(description = "总收入")
    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal totalIncome;

    @Schema(description = "出局剩余额度")
    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal totalLeftAmount;

    @Schema(description = "溢出资产")
    private List<AssetShortVO> overflowAssets;

    @Schema(description = "资产列表")
    private List<AssetShortVO> assets;

    @Schema(description = "总价值")
    @JsonSerialize(using = Money2DecimalsSerializer.class)
    public BigDecimal getTotalValue(){
        if(assets == null || assets.isEmpty()){
            return BigDecimal.ZERO;
        }
        return assets.stream().map(AssetShortVO::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
