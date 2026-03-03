package com.cs.energy.asset.api.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.cs.sp.common.base.BaseVO;
import com.cs.sp.serializer.MoneySerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author fiona
 * @date 2024/9/29 07:28
 */
@Data
@Schema(description = "资产列表")
public class AssetListVO extends BaseVO {

    @Schema(description = "账户，默认为0。0=默认账号，1=跟单保证金账户")
    private Byte type;

    @Schema(description = "代币")
    private String symbol;

    @Schema(description = "余额")
    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal balance;

    @Schema(description = "冻结")
    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal frozen;

    @Schema(description = "锁定")
    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal locked;

    @Schema(description = "可提金额")
    @JsonSerialize(using = MoneySerializer.class)
    public BigDecimal getCanWithdraw(){
        return balance.subtract(locked);
    }

    @Schema(description = "不可提金额")
    @JsonSerialize(using = MoneySerializer.class)
    public BigDecimal getNotWithdraw(){
        return locked;
    }
}
