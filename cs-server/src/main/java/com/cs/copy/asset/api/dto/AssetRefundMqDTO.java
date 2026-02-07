package com.cs.copy.asset.api.dto;

import com.cs.web.spring.redismq.event.MqMsgId;
import lombok.Data;

/**
 * @authro fun
 * @date 2025/5/27 02:59
 */
@Data
public class AssetRefundMqDTO extends MqMsgId {
    /**
     * 带mid的表示是商户的
     */
    private Long mid;
    private Long id;
    private Long refundAt;
}
