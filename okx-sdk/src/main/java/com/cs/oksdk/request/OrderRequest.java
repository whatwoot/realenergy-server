package com.cs.oksdk.request;

import com.cs.oksdk.enums.OrderType;
import com.cs.oksdk.enums.PositionsSide;
import com.cs.oksdk.enums.Side;
import com.cs.oksdk.enums.TdMode;
import com.cs.oksdk.request.base.BaseOkxRequest;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @authro fun
 * @date 2025/11/26 17:21
 */
@Data
@Builder
public class OrderRequest extends BaseOkxRequest {
    private String instId;
    /**
     * @see TdMode
     */
    private String tdMode;
    private String ccy;
    /**
     * @see Side
     */
    private String side;
    private String clOrdId;
    /**
     * @see PositionsSide
     */
    private String posSide;
    /**
     * @see OrderType
     */
    private String ordType;
    private BigDecimal sz;
    private Boolean reduceOnly;
    private String tag;
}
