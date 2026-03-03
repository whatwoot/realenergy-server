package com.cs.energy.member.api.request;

import com.cs.web.base.BaseRandomRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @authro fun
 * @date 2025/6/12 00:42
 */
@Data
@Schema(description = "修改bsc钱包")
public class GoogleBindRequest extends BaseRandomRequest {
    @Schema(description = "otp密钥")
    @NotBlank(message = "chk.common.required")
    @NotNull(message = "chk.common.required")
    private String otpSecret;
    @Schema(description = "otp码")
    @NotBlank(message = "chk.common.required")
    @NotNull(message = "chk.common.required")
    private String otpCode;
}
