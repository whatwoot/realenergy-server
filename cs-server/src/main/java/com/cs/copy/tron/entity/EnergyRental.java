package com.cs.copy.tron.entity;

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
     * 状态: 0-待处理, 1-活跃, 2-已过期, 3-已收回,5-没有合适的价格档位,6-地址没有激活
     */
    private Integer status;

    /**
     * 承租方转账交易Hash
     */
    private String requestTxHash;

    /**
     * 承租方转账Trx数量
     */
    private Long requestTrx; //

    /**
     * 代理资源交易Hash
     */
    private String delegateTxHash;

    /**
     * 收回资源交易Hash
     */
    private String reclaimTxHash;
}
