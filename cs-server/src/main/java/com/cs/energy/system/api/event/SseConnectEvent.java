package com.cs.energy.system.api.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @authro fun
 * @date 2025/6/26 17:25
 */
@Getter
public class SseConnectEvent extends ApplicationEvent {
    private Long id;
    private String sid;

    public SseConnectEvent(Object source, Long id, String sid) {
        super(source);
        this.id = id;
        this.sid = sid;
    }
}
