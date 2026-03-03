package com.cs.energy.asset.api.request;

import com.cs.sp.common.base.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @authro fun
 * @date 2026/1/2 14:30
 */
@Data
@Schema(description = "资产明细")
public class AssetTransferRequest extends BaseRequest {
    @Schema(description = "代币")
    @NotNull(message = "chk.common.required")
    @NotBlank(message = "chk.common.required")
    private String symbol;

    @Schema(description = "来源账户，0=默认账号，1=跟单保证金账户")
    @NotNull(message = "chk.common.required")
    private Byte fromType;

    @Schema(description = "目标账户，0=默认账号，1=跟单保证金账户")
    @NotNull(message = "chk.common.required")
    private Byte toType;

    @Schema(description = "数量")
    @NotNull(message = "chk.common.required")
    private BigDecimal balance;

}
