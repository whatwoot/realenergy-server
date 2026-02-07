package com.cs.web.base;

import com.cs.sp.common.base.BaseRequest;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @authro fun
 * @date 2026/1/2 18:22
 */
@Data
public class SimpleIdRequest extends BaseRequest {
    @NotNull(message = "chk.common.required")
    private Long id;
}
