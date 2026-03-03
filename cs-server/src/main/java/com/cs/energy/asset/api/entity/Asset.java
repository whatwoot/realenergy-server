package com.cs.energy.asset.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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
 * @since 2024-09-29
 */
@Getter
@Setter
@TableName("a_asset")
@Schema(name = "Asset", description = "资产")
public class Asset extends BaseDO {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "账户类型。0=默认")
    private Byte type;

    @Schema(description = "UID")
    private Long uid;

    @Schema(description = "币种")
    private String symbol;

    @Schema(description = "快照日期")
    private Integer fundYmd;

    @Schema(description = "t+1快照余额")
    private BigDecimal fundBalance;
    @Schema(description = "t+0快照余额")
    private BigDecimal fundT0Balance;

    @Schema(description = "余额")
    private BigDecimal balance;

   @Schema(description = "最后售出日期")
   private Integer lastBonusDay;

   @Schema(description = "投资时间")
   private Long investAt;

   @Schema(description = "开奖周期")
   private Integer period;

    @Schema(description = "冻结")
    private BigDecimal frozen;

    @Schema(description = "锁定")
    private BigDecimal locked;

   @Schema(description = "节点账户。1=是,0=否")
   private Byte genesis;

    private Date createTime;

    private Date updateTime;
}
