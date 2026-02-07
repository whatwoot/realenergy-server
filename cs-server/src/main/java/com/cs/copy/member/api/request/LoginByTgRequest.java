package com.cs.copy.member.api.request;

import com.cs.sp.common.base.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author fiona
 * @date 2024/9/29 08:10
 */
@Data
@Schema(description = "tg绑定/登录")
public class LoginByTgRequest extends BaseRequest {
    @Schema(description = "登录参数")
    @NotNull(message = "chk.common.required")
    @NotBlank(message = "chk.common.required")
    private String initData;
    @Schema(description = "测试环境使用,1=跳过hash校验,方便测试")
    private Byte skip;
}
