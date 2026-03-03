package com.cs.energy.asset.api.request;

import com.cs.sp.common.base.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author fiona
 * @date 2024/10/3 15:18
 */
@Data
@Schema(description = "提现申请")
public class WithdrawRequest extends BaseRequest {
    @Schema(description = "链。bsc=币安智能链")
    @NotNull(message = "chk.common.required")
    @NotBlank(message = "chk.common.required")
    private String chain;
    @Schema(description = "币种")
    @NotNull(message = "chk.common.required")
    @NotBlank(message = "chk.common.required")
    private String symbol;

    @Schema(description = "金额")
    @NotNull(message = "chk.common.required")
    private BigDecimal quantity;

    @Schema(description = "接收地址")
    private String arriveAddr;
}
