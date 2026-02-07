package com.cs.copy.evm.api.request;

import com.cs.sp.common.base.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author fiona
 * @date 2025/3/1 00:07
 */
@Data
@Schema(description = "代币列表请求")
public class SymbolListRequest extends BaseRequest {
    @Schema(description = "类型。1=充，0=提")
    private Byte type;
    @Schema(description = "代币对。")
    private String symbol;
    @Schema(description = "基准代币")
    private String baseCoin;
    @Schema(description = "链。bsc=币安智能链")
    private String chain;
}
