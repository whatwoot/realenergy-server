package com.cs.energy.member.api.vo;

import com.cs.sp.common.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fiona
 * @date 2024/9/29 21:06
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "签名nonce")
public class UserNonceVO extends BaseVO {

    @Schema(description = "id")
    private String nonce;

    @Schema(description = "邀请码")
    private Long createAt;
}
