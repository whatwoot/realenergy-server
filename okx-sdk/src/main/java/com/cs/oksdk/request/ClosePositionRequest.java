package com.cs.oksdk.request;

import com.cs.oksdk.enums.MgnMode;
import com.cs.oksdk.enums.PositionsSide;
import com.cs.oksdk.request.base.BaseOkxRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * @authro fun
 * @date 2025/11/27 16:45
 */
@Data
@Builder
public class ClosePositionRequest extends BaseOkxRequest {
    @Schema(description = "产品ID")
    private String instId;
    @Schema(description = "持仓方向")
    private PositionsSide posSide;
    @Schema(description = "保证金模式")
    private MgnMode mgnMode;
    private String ccy;
    private Boolean autoCxl;
    @Schema(description = "客户自定义ID")
    private String clOrdId;
    private String tag;
}
