package com.cs.energy.member.api.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.cs.sp.common.base.BaseVO;
import com.cs.sp.serializer.MoneySerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author fiona
 * @date 2025/2/25 21:22
 */
@Data
@Schema(description = "邀请统计信息")
public class InviteSummary extends BaseVO {
    @Schema(description = "团队总业绩")
    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal total;

    @Schema(description = "大区业绩")
    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal highest;

    @Schema(description = "小区业绩")
    @JsonSerialize(using = MoneySerializer.class)
    public BigDecimal getLowest(){
        return total.subtract(highest);
    }
}
