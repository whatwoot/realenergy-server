package com.cs.copy.member.api.vo;

import com.cs.sp.common.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author fiona
 * @date 2024/12/11 03:05
 */
@Data
@Schema(description = "团队级别列表")
public class TeamlevelListVO extends BaseVO {
    private Integer id;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "小区业绩")
    private BigDecimal smallerPerformance;

    @Schema(description = "平级奖")
    private BigDecimal sameLevelRate;
}
