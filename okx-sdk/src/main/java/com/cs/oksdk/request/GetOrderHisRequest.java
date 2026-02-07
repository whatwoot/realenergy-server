package com.cs.oksdk.request;

import com.cs.oksdk.enums.InstrumentType;
import com.cs.oksdk.request.base.BaseOkxRequest;
import lombok.Builder;
import lombok.Data;

/**
 * @authro fun
 * @date 2025/11/26 17:21
 */
@Data
@Builder
public class GetOrderHisRequest extends BaseOkxRequest {
    private InstrumentType instType;
    private String instFamily;
    private String instId;
    private String ordType;
    /**
     * 订单状态
     * canceled：撤单成功
     * filled：完全成交
     * mmp_canceled：做市商保护机制导致的自动撤单
     */
    private String state;
    /**
     * 订单种类
     * twap：TWAP自动换币
     * adl：ADL自动减仓
     * full_liquidation：强制平仓
     * partial_liquidation：强制减仓
     * delivery：交割
     * ddh：对冲减仓类型订单
     */
    private String category;
    /**
     * 请求此ID之前（更旧的数据）的分页内容，传的值为对应接口的ordId
     */
    private String after;
    /**
     * 请求此ID之后（更新的数据）的分页内容，传的值为对应接口的ordId
     */
    private String before;
    /**
     * 筛选的开始时间戳 cTime，Unix 时间戳为毫秒数格式，如 1597026383085
     */
    private String begin;
    /**
     * 筛选的结束时间戳 cTime，Unix 时间戳为毫秒数格式，如 1597027383085
     */
    private String end;
    private Integer limit;
}
