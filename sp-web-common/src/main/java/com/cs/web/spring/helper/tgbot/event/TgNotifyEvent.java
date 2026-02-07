package com.cs.web.spring.helper.tgbot.event;

import com.cs.web.spring.helper.tgbot.dto.TgNotifyDTO;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @authro fun
 * @date 2025/4/25 03:12
 */
@Getter
public class TgNotifyEvent extends ApplicationEvent {
    private TgNotifyDTO notify;

    public TgNotifyEvent(Object source, TgNotifyDTO notify) {
        super(source);
        this.notify = notify;
    }
}
