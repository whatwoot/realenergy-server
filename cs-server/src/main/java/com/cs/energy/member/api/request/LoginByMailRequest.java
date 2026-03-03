package com.cs.energy.member.api.request;

import com.cs.sp.common.base.BaseRequest;
import com.cs.web.validator.annotation.AllowByte;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author fiona
 * @date 2024/2/3 14:29
 */
@Data
@Schema(description = "邮箱登录")
public class LoginByMailRequest extends BaseRequest {
    @Schema(description = "邮箱地址")
    @NotNull(message = "chk.common.required")
    @NotBlank(message = "chk.common.required")
    @Email(message = "chk.mail.invalid")
    private String account;
    @NotBlank(message = "chk.common.required")
    @Schema(description = "密码/验证码")
    private String secret;
    @Schema(description = "类型，1=密码,2=验证码")
    @NotNull(message = "chk.common.required")
    @AllowByte(message = "chk.common.invalid", vals = {1,2})
    private Byte type;
}
