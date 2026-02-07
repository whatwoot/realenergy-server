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
@Schema(description = "修改bsc钱包")
public class BscWalletRequest extends BaseRequest {
    @Schema(description = "钱包")
    @NotNull(message = "chk.common.required")
    @NotBlank(message = "chk.common.required")
    private String account;
}
