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
@TableName("a_asset_flow")
@Schema(name = "AssetFlow", description = "资产流水")
public class AssetFlow extends BaseDO {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "账户类型。0=默认")
    private Byte type;

    @Schema(description = "UID")
    private Long uid;

    @Schema(description = "场景")
    private String scene;

    @Schema(description = "代币")
    private String symbol;

    @Schema(description = "余额变动前")
    private BigDecimal beginBalance;

    @Schema(description = "余额变动")
    private BigDecimal balance;

    @Schema(description = "冻结变动前")
    private BigDecimal beginFrozen;

    @Schema(description = "冻结变动")
    private BigDecimal frozen;

    @Schema(description = "冻结变动")
    private BigDecimal locked;

    @Schema(description = "创建于")
    private Long createAt;
    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "账期")
    private Integer ymd;
    @Schema(description = "清算日")
    private Integer clearDay;
    @Schema(description = "业务关联id")
    private Long relateId;
    private Long chainWorkId;
    @Schema(description = "业务关联备注")
    private String relateMemo;

    @Schema(description = "退款状态。1=是,0=否")
    private Byte refunded;
    @Schema(description = "退款时间")
    private Long refundAt;

    @Schema(description = "备注")
    private String memo;

    @Schema(description = "扩展参数")
    private String extParams;

    @Schema(description = "是否对外展示。1=是,0=否")
    private Byte showed;
}
