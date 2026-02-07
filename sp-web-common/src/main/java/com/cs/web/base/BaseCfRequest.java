package com.cs.web.base;

import com.cs.sp.common.base.BaseRequest;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author sb
 * @date 2025/2/21 18:18
 */
@Data
public class BaseCfRequest extends BaseRequest {
    @Hidden
    private String ip;
    @Schema(description = "Turnstile行为验证Token")
    private String turnstile;
}
