package com.cs.energy.member.api.request;

import com.cs.sp.common.base.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author quzhimin
 * @date 2024/9/29 08:10
 */
@Data
@Schema(description = "绑定邀请关系")
public class BindRequest extends BaseRequest {
    @Schema(description = "邀请码")
    @NotNull(message = "chk.common.required")
    @NotBlank(message = "chk.common.required")
    private String inviteCode;
}
