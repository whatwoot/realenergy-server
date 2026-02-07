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
public class PositionsHisRequest extends BaseOkxRequest {
    private InstrumentType instType;
    private String instId;
    private String posId;
    private String mgnMode;
    private String type;
    /**
     * 查询仓位更新 (uTime) 之后的内容，值为时间戳，Unix 时间戳为毫秒数格式，如 1597026383085
     */
    private String after;
    /**
     * 查询仓位更新 (uTime) 之前的内容，值为时间戳，Unix 时间戳为毫秒数格式，如 1597026383085
     */
    private String before;
    private String limit;
}
