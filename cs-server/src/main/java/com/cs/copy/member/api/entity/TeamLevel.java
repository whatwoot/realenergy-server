package com.cs.copy.member.api.entity;

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
 * @since 2024-12-11
 */
@Getter
@Setter
@TableName("u_team_level")
@Schema(name = "TeamLevel", description = "")
public class TeamLevel extends BaseDO{

    private Integer id;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "流水")
    private BigDecimal fundFlowAmount;
    @Schema(description = "团队流水")
    private BigDecimal teamFundFlowAmount;
    @Schema(description = "小区业绩")
    private BigDecimal smallerPerformance;

    @Schema(description = "同级邀请人数量")
    private Integer levelInviterNum;

    @Schema(description = "平级奖")
    private BigDecimal sameLevelRate;
    @Schema(description = "平级奖来源。0=上级,1=源奖励")
    private Byte sameLevelSource;
    @Schema(description = "平级奖数量限制。-1=无限,0=无,其他=相应次数")
    private Integer sameLevelLimit;

    @Schema(description = "等级奖")
    private BigDecimal levelRate;
    @Schema(description = "是否级差。1=是,0=否")
    private Byte differential;

    @Schema(description = "状态")
    private Byte status;
    private Integer weight;

    private Date createTime;

    private Date updateTime;
}
