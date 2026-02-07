package com.cs.copy.member.api.request;

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
@Schema(description = "添加账户")
public class LoginAddRequest extends BaseRequest {
    @NotBlank(message = "chk.common.required")
    @NotNull(message = "chk.common.required")
    @Schema(description = "密码（rsa加密）")
    private String secret;
}
