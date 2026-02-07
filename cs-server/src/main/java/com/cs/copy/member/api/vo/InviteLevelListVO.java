package com.cs.copy.member.api.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cs.sp.common.base.BaseDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
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
@Data
@Schema(description = "邀请级别")
public class InviteLevelListVO extends BaseDO {


    private Integer id;

    @Schema(description = "层级名")
    private String name;

    @Schema(description = "奖励比率")
    private BigDecimal prizeRate;

    @Schema(description = "奖励层级")
    private Integer level;

    @Schema(description = "邀请人数")
    private Integer inviteNum;
}
