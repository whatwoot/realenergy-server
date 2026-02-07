package com.cs.copy.member.api.vo;

import com.cs.sp.common.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author fiona
 * @date 2024/9/29 21:06
 */
@Data
@Schema(description = "用户会话信息")
public class UserInfoVO extends BaseVO {
    private Long id;
    @Schema(description = "tg用户id")
    private String tgId;
    @Schema(description = "tg昵称")
    private String username;
    @Schema(description = "tg账号")
    private String nickname;
    @Schema(description = "ton钱包")
    private String addr;
}
