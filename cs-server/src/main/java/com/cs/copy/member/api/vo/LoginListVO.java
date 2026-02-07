package com.cs.copy.member.api.vo;

import com.cs.sp.common.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author fiona
 * @date 2025/2/25 23:08
 */
@Data
@Schema(description = "登录账户列表")
public class LoginListVO extends BaseVO {
    @Schema(description = "类型。1=邮箱,2=手机,3=bsc钱包,9=PIN")
    private Byte type;
    @Schema(description = "UID")
    private Long uid;
    @Schema(description = "账号")
    private String account;
    @Schema(description = "绑定时间")
    private Long bindAt;
    @Schema(description = "状态。1=开启,0=关闭")
    private Byte status;
    @Schema(description = "备注")
    private String memo;
}
