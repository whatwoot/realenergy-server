package com.cs.sp.common.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 可控制是否脱敏的
 * @author sb
 * @date 2023/9/27 22:51
 */
@Data
public class BaseDesenseVo extends BaseVO{
    /**
     * 是否需要直接显示
     */
    @Schema(description = "是否不脱敏")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private boolean insensitive;
}
