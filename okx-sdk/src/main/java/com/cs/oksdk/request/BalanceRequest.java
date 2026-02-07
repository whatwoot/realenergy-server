package com.cs.oksdk.request;

import com.cs.oksdk.request.base.BaseOkxRequest;
import lombok.Builder;
import lombok.Data;

/**
 * @authro fun
 * @date 2025/11/26 17:12
 */
@Data
@Builder
public class BalanceRequest extends BaseOkxRequest {
    private String ccy;
}
