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
public class SymbolPriceWeb2ListRequest extends BaseRequest {
    @Schema(description = "代币对。eg:CNYMIN=人民币兑MIN,USDMIN=美元兑MIN,VNDMIN=越南盾兑MIN,KRWMIN=韩元兑换MIN")
    private String symbol;
    @Schema(description = "基准货币。eg:CNY=人民币,USD=美元,THB=泰铢,VND=越南盾,KRW=韩元")
    private String baseCurrency;
    @Schema(description = "计价货币。eg:MIN=计价用MIN")
    private String quoteCurrency;
}
