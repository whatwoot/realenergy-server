package com.cs.energy.chain.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author gpthk
 * @since 2024-11-23
 */
@Getter
@Setter
@TableName("c_chain_address")
@Schema(name = "ChainAddress", description = "")
public class ChainAddress implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "地址类型。1=充值,2=归集,3=手续费,4=提现")
    private Byte type;
    @Schema(description = "链")
    private String chain;
    @Schema(description = "钱包版本")
    private String ver;
    @Schema(description = "钱包walletId")
    private Long walletId;
    @Schema(description = "代币")
    private String symbol;
    private String addr;
    private String showAddr;
    private String usdtAddr;

    private BigDecimal balance;
    private BigDecimal usdtBalance;
    @Schema(description = "是否需要刷新")
    private Byte needRefresh;
    @Schema(description = "刷新时间。需要刷新时，指定刷新的时间")
    private Long updateAt;
    @Schema(description = "状态。1=有效，0=无效")
    private Byte status;
    @Schema(description = "备注")
    private String memo;
    @Schema(description = "加密密钥")
    private String privKey;

    private Integer weight;

    private Date createTime;
}
