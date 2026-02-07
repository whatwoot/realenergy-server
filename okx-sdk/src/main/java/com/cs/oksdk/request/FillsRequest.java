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
public class FillsRequest extends BaseOkxRequest {
    private InstrumentType instType;
    private String instFamily;
    private String instId;
    private String ordId;
    private String subType;
    private String after;
    private String before;
    private String begin;
    private String end;
    private String limit;
}
