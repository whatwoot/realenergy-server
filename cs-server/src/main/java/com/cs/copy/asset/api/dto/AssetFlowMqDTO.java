package com.cs.copy.asset.api.dto;

import com.cs.web.spring.redismq.event.MqMsgId;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @authro fun
 * @date 2025/5/27 02:59
 */
@Data
public class AssetFlowMqDTO extends MqMsgId {
    private Long id;
    private Long mid;
    private String scene;
    private Long uid;
    private String symbol;
    private BigDecimal balance;
    private Long createAt;
}
