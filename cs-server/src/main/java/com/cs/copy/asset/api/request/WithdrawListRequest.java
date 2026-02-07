package com.cs.copy.asset.api.request;

import com.cs.web.base.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author fiona
 * @date 2024/10/3 15:18
 */
@Data
@Schema(description = "资产明细")
public class WithdrawListRequest extends BasePageRequest {
    @Schema(description = "币种")
    private String symbol;
    @Schema(description = "提币状态。0=待处理,1=已确认,2=待确认")
    private Byte status;
    @Schema(description = "开始日期",example = "yyyyMMdd")
    private Integer startDay;
    @Schema(description = "截止日期",example = "yyyyMMdd")
    private Integer endDay;
}
