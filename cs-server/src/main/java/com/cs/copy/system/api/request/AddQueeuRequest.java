package com.cs.copy.system.api.request;

import com.cs.sp.common.base.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "邀请")
public class AddQueeuRequest extends BaseRequest {
    @Schema(description = "理财记录id")
    private Long id;
    @Schema(description = "用户id")
    private Long uid;
    @Schema(description = "用户邮箱")
    private String email;
}
