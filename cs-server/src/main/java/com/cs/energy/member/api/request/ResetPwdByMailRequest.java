package com.cs.energy.member.api.request;

import com.cs.sp.common.base.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author fiona
 * @date 2024/2/3 14:29
 */
@Data
@Schema(description = "邮箱重置密码")
public class ResetPwdByMailRequest extends BaseRequest {
    @Schema(description = "业务流水码。根据业务决定")
    private Long id;
    @NotBlank(message = "chk.common.required")
    @NotNull(message = "chk.common.required")
    @Schema(description = "账户")
    private String account;
    @NotBlank(message = "chk.common.required")
    @NotNull(message = "chk.common.required")
    @Schema(description = "密码")
    private String secret;
}
