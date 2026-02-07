package com.cs.copy.system.api.request;

import com.cs.sp.common.base.BaseRequest;
import com.cs.web.validator.annotation.AllowBigdecimal;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Schema(description = "邀请")
public class AddInvestRequest extends BaseRequest {
    @Schema(description = "用户id")
    private Long uid;
    @Schema(description = "邮箱")
    private String email;
    @NotNull(message = "chk.common.required")
    @AllowBigdecimal(vals = {"100","1500"},message = "chk.common.invalid")
    private BigDecimal amount;
    @NotNull(message = "chk.common.required")
    @Range(min = 1, message = "chk.common.invalid")
    private Integer num;
    @Schema(description = "是否参与公排。1=是,0=否")
    private Integer queue;
}
