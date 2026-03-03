package com.cs.energy.evm.api.vo;

import com.cs.sp.common.base.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author fiona
 * @date 2025/3/1 00:05
 */
@Data
@Schema(description = "代币列表响应")
public class SymbolListVO extends BaseRequest {
    private Integer id;

    @Schema(description = "代币类型。1=充,0=提")
    private Byte type;

    @Schema(description = "链")
    private String chain;

    @Schema(description = "链类型")
    private String chainEngine;

    @Schema(description = "代币")
    private String symbol;

    @Schema(description = "基准货币")
    private String baseCoin;

    @Schema(description = "计价货币")
    private String quoteCoin;

    @Schema(description = "可充。1=是,0=否")
    private Byte canRecharge;

    @Schema(description = "可提。1=是,0=否")
    private Byte canWithdraw;

    @Schema(description = "汇率")
    private BigDecimal exchangeRate;

    @Schema(description = "合约地址")
    private String symbolCa;
    @Schema(description = "基准代币地址")
    private String baseCa;
    @Schema(description = "计价代币地址")
    private String quoteCa;

    @Schema(description = "交易对精度")
    private Integer symbolDecimals;
    @Schema(description = "基准代币精度")
    private Integer baseDecimals;
    @Schema(description = "计价代币精度")
    private Integer quoteDecimals;

    @Schema(description = "最小充值金额")
    private BigDecimal rechargeMinAmount;
    @Schema(description = "最小提现金额")
    private BigDecimal withdrawMinAmount;

    @Schema(description = "手续费")
    private BigDecimal feeMinAmount;
    @Schema(description = "提现金额为倍数。0=否,1=是")
    private Byte withdrawAmountMultipled;
    @Schema(description = "手续费率")
    private BigDecimal feeRate;
    @Schema(description = "阶梯手续费")
    private String fees;

    private Byte collected;
    private BigDecimal collectMinAmount;
    private BigDecimal collectGas;
    @Schema(description = "gas代币")
    private String gasCoin;

    @Schema(description = "权重")
    private Integer weight;
}
