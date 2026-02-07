package com.cs.copy.system.api.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.cs.sp.common.base.BaseVO;
import com.cs.sp.serializer.MoneySerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @authro fun
 * @date 2025/5/25 16:32
 */
@Data
@Schema(description = "小区实时业绩")
public class SmallTeamPerformanceVO extends BaseVO {
    private Long uid;
    private String addr;
    @Schema(description = "公告标题")
    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal amount;
    private Long createAt;
}
