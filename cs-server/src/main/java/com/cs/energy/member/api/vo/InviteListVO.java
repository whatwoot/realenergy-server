package com.cs.energy.member.api.vo;

import com.cs.sp.common.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author fiona
 * @date 2024/12/11 03:01
 */
@Data
@Schema(description = "邀请级别列表")
public class InviteListVO extends BaseVO{
    private Integer id;

    @Schema(description = "层级名")
    private String name;

    @Schema(description = "奖励比率")
    private BigDecimal prizeRate;

    @Schema(description = "奖励层级")
    private Integer level;
}
