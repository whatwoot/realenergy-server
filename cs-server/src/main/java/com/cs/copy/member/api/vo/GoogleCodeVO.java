package com.cs.copy.member.api.vo;

import com.cs.sp.common.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @authro fun
 * @date 2025/6/12 00:03
 */
@Data
@Schema(description = "用户绑定tg信息")
public class GoogleCodeVO extends BaseVO {
    @Schema(description = "otp密钥")
    private String otpSecret;
    @Schema(description = "二维码文本")
    private String otpSecretUrl;
}
