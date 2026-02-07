package com.cs.oksdk.request;

import com.cs.oksdk.request.base.BaseOkxRequest;
import lombok.Builder;
import lombok.Data;

/**
 * @authro fun
 * @date 2025/11/28 16:02
 */
@Data
@Builder
public class AssetBillsRequest extends BaseOkxRequest {
    private String type;
    private String clientId;
    private String after;
    private String before;
    private String limit;
}
