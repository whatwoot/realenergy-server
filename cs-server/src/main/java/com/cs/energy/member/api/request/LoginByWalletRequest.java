package com.cs.energy.member.api.request;

import com.cs.sp.common.base.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author jamie
 * @date 2024/2/3 14:29
 */
@Data
@Schema(description = "钱包登录")
public class LoginByWalletRequest extends BaseRequest {
    @Schema(description = "地址")
    private String addr;
    @Schema(description = "unix时间戳")
    private Long time;
    @Schema(description = "nonce。/auth/nonce接口获取")
    private String nonce;
    @Schema(description = "自定义参与签名的消息")
    private String msg;
    @Schema(description = "签名")
    private String sign;
}
