package com.cs.energy.asset.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cs.sp.common.base.BaseDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
 * @since 2024-11-25
 */
@NoArgsConstructor
@Getter
@Setter
@TableName("a_withdraw_detail")
@Schema(name = "WithdrawDetail", description = "")
public class WithdrawDetail extends BaseDO{

    public WithdrawDetail(Long id) {
        this.id = id;
    }

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "流水id")
    private Long flowId;
    @Schema(description = "链")
    private String chain;

    @Schema(description = "uid")
    private Long uid;

    @Schema(description = "钱包地址id")
    private Long addressId;

    @Schema(description = "金额")
    private BigDecimal amount;

    @Schema(description = "发送者")
    private String fromAddr;

    @Schema(description = "接收者")
    private String toAddr;

    @Schema(description = "状态。0=待发送,1=已完成,2=发送中")
    private Byte status;

    @Schema(description = "事务hash")
    private String hash;

    @Schema(description = "发送时间")
    private Long sendAt;

    @Schema(description = "确认时间")
    private Long confirmAt;
    @Schema(description = "确认区块号")
    private Long confirmBlockNo;

    @Schema(description = "备注")
    private String memo;
    @Schema(description = "序号")
    private Integer seq;

    private Date createTime;

    private Date updateTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WithdrawDetail that = (WithdrawDetail) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
