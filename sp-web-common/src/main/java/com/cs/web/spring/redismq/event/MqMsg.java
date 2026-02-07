package com.cs.web.spring.redismq.event;

import lombok.Data;

/**
 * @authro fun
 * @date 2025/5/24 19:44
 */
@Data
public class MqMsg extends MqMsgId {
    private String data;
}
