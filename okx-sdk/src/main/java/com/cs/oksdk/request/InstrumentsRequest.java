package com.cs.oksdk.request;

import com.cs.oksdk.enums.InstrumentType;
import com.cs.oksdk.request.base.BaseOkxRequest;
import lombok.Builder;
import lombok.Data;

/**
 * @authro fun
 * @date 2025/11/28 20:33
 */
@Data
@Builder
public class InstrumentsRequest extends BaseOkxRequest {
    private InstrumentType instType;
    private String instFamily;
    private String instId;
}
