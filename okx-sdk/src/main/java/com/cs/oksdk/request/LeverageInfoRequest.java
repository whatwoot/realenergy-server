package com.cs.oksdk.request;

import com.cs.oksdk.request.base.BaseOkxRequest;
import lombok.Builder;
import lombok.Data;

/**
 * @authro fun
 * @date 2025/11/29 00:37
 */
@Data
@Builder
public class LeverageInfoRequest extends BaseOkxRequest {
    private String instId;
    private String ccy;
    private String mgnMode;
}
