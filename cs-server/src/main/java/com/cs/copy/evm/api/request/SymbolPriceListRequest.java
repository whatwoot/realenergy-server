package com.cs.copy.evm.api.request;

import com.cs.sp.common.base.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author fiona
 * @date 2025/3/1 00:07
 */
@Data
@Schema(description = "代币汇率请求")
public class SymbolPriceListRequest extends BaseRequest {
    @Schema(description = "代币")
    private String symbol;
    @Schema(description = "链。bsc=币安智能链,web2=法币")
    private String chain;
}
