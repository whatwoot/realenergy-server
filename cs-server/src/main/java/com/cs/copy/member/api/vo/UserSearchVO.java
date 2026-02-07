package com.cs.copy.member.api.vo;

import com.cs.sp.common.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author fiona
 * @date 2024/9/29 21:06
 */
@Data
@Schema(description = "用户详细信息")
public class UserSearchVO extends BaseVO {

    @Schema(description = "id")
    private Long id;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "昵称。如ens")
    private String fullName;

    @Schema(description = "头像url")
    private String photoUrl;

    @Schema(description = "邀请码")
    private String inviteCode;
}
