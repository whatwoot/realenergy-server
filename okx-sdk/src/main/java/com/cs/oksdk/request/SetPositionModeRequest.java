package com.cs.oksdk.request;

import com.cs.oksdk.request.base.BaseOkxRequest;
import lombok.Builder;
import lombok.Data;

/**
 * @authro fun
 * @date 2025/11/29 01:37
 */
@Data
@Builder
public class SetPositionModeRequest extends BaseOkxRequest {
    /**
     * 持仓方式
     * long_short_mode：开平仓模式 net_mode：买卖模式
     * 仅适用交割/永续
     */
    private String posMode;
}
