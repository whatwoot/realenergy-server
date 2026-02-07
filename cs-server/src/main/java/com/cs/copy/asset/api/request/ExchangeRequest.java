package com.cs.copy.asset.api.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.cs.sp.common.base.BaseRequest;
import com.cs.web.base.BasePinRequest;
import com.cs.web.validator.annotation.AllowString;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Schema(description = "闪兑请求")
public class ExchangeRequest extends BaseRequest {
    @NotBlank(message = "chk.common.required")
    @NotNull(message = "chk.common.required")
    @AllowString(message = "chk.common.invalid", vals = {"GCC"})
    @Schema(description = "转出代币")
    private String symbol;
    @NotNull(message = "chk.common.required")
    @Schema(description = "转出数量")
    private BigDecimal quantity;
    @NotBlank(message = "chk.common.required")
    @NotNull(message = "chk.common.required")
    @AllowString(message = "chk.common.invalid", vals = {"USDT"})
    @Schema(description = "转入代币")
    private String arriveSymbol;
    @Hidden
    @JsonIgnore
    private Long uid;
    @Hidden
    @JsonIgnore
    private String relateMemo;
    @Hidden
    @JsonIgnore
    private Long createAt;
}
