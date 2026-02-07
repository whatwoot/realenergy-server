package com.cs.oksdk.request;

import com.cs.oksdk.request.base.BaseOkxRequest;
import lombok.Builder;
import lombok.Data;

/**
 * @authro fun
 * @date 2025/11/26 17:21
 */
@Data
@Builder
public class GetOrderRequest extends BaseOkxRequest {
    private String instId;
    private String ordId;
    private String clOrdId;
}
