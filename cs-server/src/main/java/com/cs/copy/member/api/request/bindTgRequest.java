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
@Schema(description = "登录")
public class bindTgRequest extends BaseRequest {
    @Schema(description = "签名")
    @NotNull(message = "chk.common.required")
    private Proof proof;
    @Schema(description = "钱包")
    @NotNull(message = "chk.common.required")
    private Wallet wallet;
    @Schema(description = "测试环境使用,1=跳过hash校验,方便测试")
    private Byte skip;

    @Data
    public static class Proof {
        @NotNull(message = "chk.common.required")
        private Long timestamp;
        @NotNull(message = "chk.common.required")
        private Domain domain;
        @NotNull(message = "chk.common.required")
        @NotBlank(message = "chk.common.required")
        private String signature; // Base64UrlSafe
        @NotNull(message = "chk.common.required")
        @NotBlank(message = "chk.common.required")
        private String payload;   // plain
    }

    @Data
    public static class Wallet {
        @NotNull(message = "chk.common.required")
        private String address;
        @NotNull(message = "chk.common.required")
        private String publicKey;
        @NotNull(message = "chk.common.required")
        private Integer chain;
        @NotNull(message = "chk.common.required")
        private String walletStateInit; //base64UrlSafe
    }

    @Data
    public static class Domain {
        @NotNull(message = "chk.common.required")
        private Integer lengthBytes;
        @NotNull(message = "chk.common.required")
        private String value;
    }
}
