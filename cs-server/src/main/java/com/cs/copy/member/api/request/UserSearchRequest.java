package com.cs.copy.member.api.request;

import com.cs.sp.common.base.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @authro fun
 * @date 2025/6/21 14:24
 */
@Data
@Schema(description = "用户信息查询")
public class UserSearchRequest extends BaseRequest {
    @Schema(description = "关键词")
    @NotNull(message = "chk.common.required")
    @NotBlank(message = "chk.common.required")
    private String keyword;
}
