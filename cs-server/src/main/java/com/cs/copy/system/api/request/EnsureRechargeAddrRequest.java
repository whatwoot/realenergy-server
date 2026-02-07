package com.cs.copy.system.api.request;

import com.cs.copy.member.api.enums.ChainEnum;
import com.cs.sp.common.base.BaseRequest;
import com.cs.web.validator.annotation.AllowString;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Schema(description = "生成用户钱包地址")
public class EnsureRechargeAddrRequest extends BaseRequest {
    private Boolean force = Boolean.FALSE;
    private Long uid;
    @AllowString(message = "chk.common.invalid", vals = {"USDT"})
    private String symbol;
    @AllowString(message = "chk.common.invalid", vals = {"bsc"})
    @NotBlank(message = "chk.common.required")
    @NotNull(message = "chk.common.required")
    private String chain = ChainEnum.BSC.getCode();
}
