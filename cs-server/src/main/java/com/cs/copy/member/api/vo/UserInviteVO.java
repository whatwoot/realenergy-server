package com.cs.copy.member.api.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.cs.sp.common.base.BaseVO;
import com.cs.sp.serializer.MailSenseSerializer;
import com.cs.sp.serializer.NameSenseSerializer;
import com.cs.sp.util.StringUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * @author fiona
 * @date 2024/9/29 21:06
 */
@Data
@Schema(description = "用户详细信息")
public class UserInviteVO extends BaseVO {

    @Schema(description = "邮箱")
    @JsonSerialize(using = MailSenseSerializer.class)
    private String email;

    @Schema(description = "昵称。如ens")
    @JsonSerialize(using = NameSenseSerializer.class, nullsUsing = NameSenseSerializer.class)
    private String name;

    @Schema(description = "头像url")
    private String photoUrl;

    @Schema(description = "显示名称")
    public String getShowName() {
        if (StringUtils.hasText(name)) {
            return StringUtil.senseName(name);
        }
        return StringUtil.senseMail(email);
    }
}
