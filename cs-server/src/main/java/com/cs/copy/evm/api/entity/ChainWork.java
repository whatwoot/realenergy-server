package com.cs.copy.evm.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cs.sp.common.base.BaseDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

/**
 * <p>
 *
 * </p>
 *
 * @author gpthk
 * @since 2024-12-21
 */
@Getter
@Setter
@TableName("c_chain_work")
@Schema(name = "ChainWork", description = "")
public class ChainWork extends BaseDO {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "类型。1=结算转LP")
    private Byte type;

    @Schema(description = "链")
    private String chain;

    @Schema(description = "发起地址")
    private String fromAddr;

    @Schema(description = "接收地址")
    private String toAddr;

    @Schema(description = "代币")
    private String symbol;

    @Schema(description = "金额")
    private BigDecimal amount;

    @Schema(description = "合约")
    private String contract;

    @Schema(description = "参数")
    private String param;

    @Schema(description = "gas费")
    private BigDecimal gasPrice;
    @Schema(description = "gas费")
    private BigDecimal gasFee;

    @Schema(description = "状态。1=有效,0=无效")
    private Byte status;

    @Schema(description = "事务状态。0=待处理,1=已完成,2=待确认,3=失败")
    private Byte txStatus;

    @Schema(description = "区块号")
    private Long blockNo;

    @Schema(description = "事务hash")
    private String hash;

    @Schema(description = "创建时间")
    private Long createAt;

    @Schema(description = "排队时间")
    private Long queueAt;

    @Schema(description = "区块时间")
    private Long blockTime;

    @Schema(description = "确认于")
    private Long confirmAt;

    @Schema(description = "确认区块 ")
    private Long confirmBlockNo;

    @Schema(description = "业务id")
    private Long relateId;

    @Schema(description = "事务状态。1=成功,0=失败")
    private Byte receiptStatus;

    @Schema(description = "事务错误原因")
    private String errMsg;

    @Schema(description = "业务处理状态。1=已处理，0=未处理，2=失败")
    private Byte processed;
    @Schema(description = "排队用，定时任务处理不早于")
    private Long processAt;

    @Schema(description = "业务处理备注")
    private String processMsg;

    @Schema(description = "归集状态。1=已处理，0=无须处理，2=待检查")
    private Byte collected;
    @Schema(description = "排队用，定时任务处理不早于")
    private Long collectAt;

    @Schema(description = "归集消息")
    private String collectMsg;

    @Schema(description = "备注")
    private String memo;

    private Date createTime;

    private Date updateTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChainWork chainWork = (ChainWork) o;
        return id.equals(chainWork.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
