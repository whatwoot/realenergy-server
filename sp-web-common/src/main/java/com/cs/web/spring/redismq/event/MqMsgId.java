package com.cs.web.spring.redismq.event;

import lombok.Data;

import java.io.Serializable;

/**
 * 暂时使用Long型id
 * @authro fun
 * @date 2025/5/24 19:44
 */
@Data
public class MqMsgId implements Serializable {
    private Long msgId;
}
