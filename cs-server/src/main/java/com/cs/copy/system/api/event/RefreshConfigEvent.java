package com.cs.copy.system.api.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author fiona
 * @date 2024/12/2 17:21
 */
@Getter
public class RefreshConfigEvent extends ApplicationEvent {

    public RefreshConfigEvent(Object source) {
        super(source);
    }
}
