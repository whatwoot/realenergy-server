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
public class SetAccountLevelRequest extends BaseOkxRequest {
    /**
     * 账户模式
     * 2: 合约模式
     * 3: 跨币种保证金模式
     * 4: 组合保证金模式
     */
    private String acctLv;
    private String lever;
}
