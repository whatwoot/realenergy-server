package com.cs.copy.member.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cs.sp.common.base.BaseDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

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
@TableName("u_invite_level")
@Schema(name = "InviteLevel", description = "")
public class InviteLevel extends BaseDO {


    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "层级名")
    private String name;

    @Schema(description = "奖励比率")
    private BigDecimal prizeRate;

    @Schema(description = "奖励层级")
    private Integer level;
    @Schema(description = "邀请人数")
    private Integer inviteNum;
    private Byte status;

    private Integer weight;
}
