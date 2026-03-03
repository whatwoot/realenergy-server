package com.cs.energy.tron.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 能量租赁记录实体
 */
@Data
@TableName("tron_energy_rental")
public class EnergyRental implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 转账地址
     */
    private String fromAddress;

    /**
     * 承租方地址
     */
    private String rentAddress;

    /**
     * 能量提供方地址
     */
    private String providerAddress;

    /**
     * 支付金额 (TRX, 单位: SUN)
     */
    private BigDecimal price;

    /**
     * 租用能量数量
     */
    private Long energyAmount;
    private Long energyTrxAmount;

    /**
     * 租用时长 (毫秒)
     */
    private Long duration;

    /**
     * 创建时间戳
     */
    private Long createAt;

    /**
     * 到期时间戳
     */
    private Long expireAt;

    /**
     * 状态: 0-待处理, 1-已第三方下单, 2-已租, 3-已过期, 4-已收回,5-没有合适的价格档位,6-地址没有激活，7-订单失败
     */
    private Integer status;
    // 0-平台出租，1-gasstation出租
    private Integer lessorType;

    private Integer retries; // 重试次数

    /**
     * 承租方转账交易Hash
     */
    private String requestTxHash;

    private String traderNo; // 业务方订单号

    /**
     * 承租方转账Trx数量
     */
    private Long requestTrx; //
    private Long expenseTrx; // 第三方平台消耗的trx

    /**
     * 代理资源交易Hash
     */
    private String delegateTxHash;

    /**
     * 收回资源交易Hash
     */
    private String reclaimTxHash;

    /**
     * 人工处理状态: 0-未处理, 1-已补租, 2-已退款, 3-处理失败
     */
    private Integer handlingStatus;

    private String handlingRemark; // 人工处理备注
}
