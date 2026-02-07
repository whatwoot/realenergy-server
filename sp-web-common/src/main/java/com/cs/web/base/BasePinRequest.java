package com.cs.web.base;

import com.cs.sp.common.base.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author sb
 * @date 2025/2/21 18:18
 */
@Data
public class BasePinRequest extends BaseRequest {
    @Schema(description = "PIN码（rsa加密）")
    @NotBlank(message = "chk.common.required")
    @NotNull(message = "chk.common.required")
    private String secret;
}
