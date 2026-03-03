package com.cs.energy.system.api.request;

import com.cs.sp.common.base.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author fiona
 * @date 2024/3/10 22:01
 */
@Data
@Schema(description = "设置交易币种信息")
public class BuyCoinRequest extends BaseRequest {
    @Schema(description = "币种")
    @NotBlank(message = "chk.common.required")
    private String symbol;
    @Schema(description = "币安对应的交易对")
    @NotBlank(message = "chk.common.required")
    private String bnSymbol;
    @Schema(description = "币安交易对的精度，（下单精度超过时会失败）")
    @NotNull(message = "chk.common.required")
    private Integer bnPrecision;
    @NotNull(message = "chk.common.required")
    @Schema(description = "币安交易对的精度，买到代币的精度")
    private Integer bnBasePrecision;
    @NotNull(message = "chk.common.required")
    @Schema(description = "权重")
    private Integer weight;
    @Schema(description = "1=有效，0=无效")
    private Byte status;
}
