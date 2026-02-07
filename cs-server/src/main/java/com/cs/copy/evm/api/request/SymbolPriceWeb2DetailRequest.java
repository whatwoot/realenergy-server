package com.cs.copy.evm.api.request;

import com.cs.sp.common.base.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author fiona
 * @date 2025/3/1 00:07
 */
@Data
@Schema(description = "代币汇率详情请求")
public class SymbolPriceWeb2DetailRequest extends BaseRequest {
    @Schema(description = "基准货币。eg:CNY=人民币,USD=美元,THB=泰铢,VND=越南盾,KRW=韩元")
    @NotNull(message = "chk.common.required")
    @NotBlank(message = "chk.common.required")
    private String currency;
}
