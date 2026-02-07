package com.cs.copy.asset.api.vo;

import com.cs.sp.common.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

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
public class WithdrawFlowConfirmVO extends BaseVO {

    @Schema(description = "id")
    private Long id;

    @Schema(description = "UID")
    private Long uid;

    @Schema(description = "状态。0=已提交，1=已完成，2=确认中")
    private Byte status;

    @Schema(description = "领取的tx")
    private String claimTx;

    @Schema(description = "领取时间")
    private Long claimAt;

    @Schema(description = "确认领取时间")
    private Long confirmAt;

}
