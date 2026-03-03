package com.cs.energy.system.api.vo;

import com.cs.sp.common.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author fiona
 * @date 2024/12/9 13:48
 */
@Data
@Schema(description = "全局配置")
public class GlobalConfigVO extends BaseVO {
    @Schema(description = "ton链usdt")
    private String usdtAddr;
    @Schema(description = "bsc链usdt")
    private String evmUsdtAddr;
    @Schema(description = "bsc链usdt")
    private String exchangeAddr;
    @Schema(description = "ton链手续费地址-回弹接收地址")
    private String feeAddr;
    @Schema(description = "ton链手续费地址-回弹接收地址")
    private BigDecimal withdrawMin;
    @Schema(description = "ton链手续费地址-回弹接收地址")
    private BigDecimal withdrawFee;
    @Schema(description = "提现手续费比例")
    private BigDecimal withdrawFeeRate;
    @Schema(description = "提现金额小数位精度")
    private Integer withdrawDecimal;
    @Schema(description = "ton链提现事务gas费")
    private BigDecimal withdrawTxFee;
    @Schema(description = "ton链提现至少预留费用")
    private BigDecimal withdrawTxFeeTotal;
    @Schema(description = "ton链节点")
    private String tonEndpoint;
    @Schema(description = "ton链网络。testnet=测试,mainnet=生产")
    private String tonNetwork;
    private BigDecimal exchangeTonAmount;
    private Integer exchangeDecimals;
    @Schema(description = "最小充值金额")
    private BigDecimal depositMinAmount;
    /**
     * 中奖债券的赔率。
     */
    @Schema(description = "中奖时债券赔率")
    private BigDecimal oddsRate;
    private BigDecimal withdrawAuditAmount;
}
