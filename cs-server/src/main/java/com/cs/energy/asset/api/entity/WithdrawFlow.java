package com.cs.energy.asset.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cs.sp.common.base.BaseDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 提现流水
 * </p>
 * 实现tostring和hashcode用于比较
 * @author gpthk
 * @since 2024-10-03
 */
@Getter
@Setter
@TableName("a_withdraw_flow")
@Schema(name = "WithdrawFlow", description = "提现流水")
public class WithdrawFlow  extends BaseDO {

    @Schema(description = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "UID")
    private Long uid;

    @Schema(description = "提现的链")
    private String chain;

    @Schema(description = "币种")
    private String symbol;

    @Schema(description = "提现数量")
    private BigDecimal quantity;

    @Schema(description = "实际到账")
    private BigDecimal arriveQuantity;

    @Schema(description = "到账价值")
    private BigDecimal arriveValue;

    @Schema(description = "到账币种")
    private String arriveSymbol;

    @Schema(description = "到账地址")
    private String arriveAddr;

    @Schema(description = "审核状态。1=已审核,0=待审核")
    private Byte auditStatus;
    @Schema(description = "审核于")
    private Long auditAt;

    @Schema(description = "状态。0=已提交，1=已完成，2=确认中")
    private Byte status;

    @Schema(description = "手续费")
    private BigDecimal fee;

    @Schema(description = "汇率")
    private BigDecimal exchangeRate;

    @Schema(description = "创建于")
    private Long createAt;
    @Schema(description = "创建日期")
    private Integer ymd;

    @Schema(description = "手续费清算")
    private Integer clearDay;

    @Schema(description = "拆分事务数量")
    private Integer txNum;

    @Schema(description = "领取的tx")
    private String claimTx;

    @Schema(description = "参数")
    private String params;

    @Schema(description = "领取时间")
    private Long claimAt;

    @Schema(description = "确认领取时间")
    private Long confirmAt;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "更新时间")
    private Date updateTime;

    @Schema(description = "删除标记。0=未删除,其他=删除")
    @TableLogic(delval = "id")
    private Long deleted;
}
