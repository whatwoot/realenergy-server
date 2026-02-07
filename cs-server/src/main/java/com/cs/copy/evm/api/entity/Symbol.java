package com.cs.copy.evm.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cs.sp.common.base.BaseDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author gpthk
 * @since 2024-12-24
 */
@Getter
@Setter
@TableName("c_symbol")
@Schema(name = "Symbol", description = "")
public class Symbol extends BaseDO {

    @TableId(value = "id", type = IdType.AUTO)
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

    @Schema(description = "可充")
    private Byte canRecharge;

    @Schema(description = "可提")
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
    @Schema(description = "手续费率")
    private BigDecimal feeRate;
    @Schema(description = "手续费")
    private BigDecimal feeMinAmount;
    @Schema(description = "提现金额为倍数。0=否,1=是")
    private Byte withdrawAmountMultipled;
    @Schema(description = "阶梯手续费")
    private String fees;

    private Byte collected;
    private BigDecimal collectMinAmount;
    private BigDecimal collectGas;
    private String gasCoin;
    private String activePeriod;

    @Schema(description = "权重")
    private Integer weight;

    @Schema(description = "是否展示。1=是,0=否")
    private Byte showed;
    @Schema(description = "状态。1=有效,0=无效")
    private Byte status;

    @Schema(description = "创建时间")
    private Date createTime;
    @Schema(description = "更新时间")
    private Date updateTime;
}
