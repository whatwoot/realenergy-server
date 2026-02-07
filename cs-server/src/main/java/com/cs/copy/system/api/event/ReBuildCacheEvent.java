package com.cs.copy.system.api.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author fiona
 * @date 2024/12/2 17:21
 */
public class ReBuildCacheEvent extends ApplicationEvent {

    public ReBuildCacheEvent(Object source) {
        super(source);
    }
}
