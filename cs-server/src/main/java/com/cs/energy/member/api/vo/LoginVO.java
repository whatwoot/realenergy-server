package com.cs.energy.member.api.vo;

import com.cs.sp.common.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fiona
 * @date 2024/9/29 08:24
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "登录结果")
public class LoginVO extends BaseVO {
    @Schema(description = "登录token")
    private String accessToken;
    @Schema(description = "过期于")
    private Long expiredAt;

    public LoginVO(String accessToken) {
        this.accessToken = accessToken;
    }
}
