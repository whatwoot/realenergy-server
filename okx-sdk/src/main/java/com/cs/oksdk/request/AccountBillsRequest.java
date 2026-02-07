package com.cs.oksdk.request;

import com.cs.oksdk.enums.InstrumentType;
import com.cs.oksdk.request.base.BaseOkxRequest;
import lombok.Builder;
import lombok.Data;

/**
 * @authro fun
 * @date 2025/11/28 16:02
 */
@Data
@Builder
public class AccountBillsRequest extends BaseOkxRequest {
    private InstrumentType instType;
    private String instId;
    private String ccy;
    private String mgnMode;
    private String ctType;
    private String type;
    private String subType;
    private String before;
    private String after;
    private String begin;
    private String end;
    private String limit;
}
