package com.cs.copy.asset.api.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.cs.sp.common.base.BaseVO;
import com.cs.sp.serializer.MoneySerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * <p>
 * 提现流水
 * </p>
 *
 * @author gpthk
 * @since 2024-10-03
 */
@Getter
@Setter
@Schema(name = "WithdrawFlow", description = "提现流水")
public class WithdrawFlowVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    @Schema(description = "id")
    private Long id;

    @Schema(description = "UID")
    private Long uid;

    @Schema(description = "链")
    private String chain;

    @Schema(description = "币种")
    private String symbol;

    @Schema(description = "提现数量")
    @JsonSerialize(using= MoneySerializer.class)
    private BigDecimal quantity;

    @Schema(description = "实际到账")
    @JsonSerialize(using= MoneySerializer.class)
    private BigDecimal arriveQuantity;

    @Schema(description = "状态。0=已提交，1=已完成，2=确认中")
    private Byte status;

    @Schema(description = "手续费")
    @JsonSerialize(using= MoneySerializer.class)
    private BigDecimal fee;
    @Schema(description = "创建时间")
    private Long createAt;
    @Schema(description = "到账时间")
    private Long claimAt;
}
