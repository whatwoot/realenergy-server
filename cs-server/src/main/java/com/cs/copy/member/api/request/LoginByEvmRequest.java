package com.cs.copy.member.api.request;

import com.cs.sp.common.base.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author fiona
 * @date 2024/2/3 14:29
 */
@Data
@Schema(description = "evm钱包登录")
public class LoginByEvmRequest extends BaseRequest {
    @NotBlank(message = "chk.common.required")
    @Schema(description = "钱包地址")
    @Length(min = 1, max = 42, message = "chk.common.sizeRange")
    private String addr;
    @Schema(description = "绑定码")
    private String bind;
    @NotNull(message = "chk.common.required")
    @Schema(description = "时间")
    private Long time;
    @NotNull(message = "chk.common.required")
    @Schema(description = "消息")
    private String msg;
    @NotNull(message = "chk.common.required")
    @Schema(description = "nonce")
    private String nonce;
    @NotNull(message = "chk.common.required")
    @Schema(description = "签名")
    private String sign;
}
