package com.cs.web.spring.redismq.event;

import com.cs.web.spring.redismq.DefaultRedisMqConsumer;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 默认Redis的mq消息事件
 * 可以使用
 * @see DefaultRedisMqConsumer
 * @authro fun
 * @date 2025/5/24 19:49
 */
@Getter
public class MsgEvent extends ApplicationEvent {
    private MqMsg mqMsg;

    public MsgEvent(Object source, MqMsg mqMsg) {
        super(source);
        this.mqMsg = mqMsg;
    }
}
