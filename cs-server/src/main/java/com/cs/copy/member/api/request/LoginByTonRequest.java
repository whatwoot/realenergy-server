package com.cs.copy.member.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author fiona
 * @date 2024/9/29 08:10
 */
@Data
@Schema(description = "登录")
public class LoginByTonRequest extends bindTgRequest {
    @Schema(description = "绑定码")
    private String bind;
}
