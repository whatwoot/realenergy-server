package com.cs.copy.evm.api.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author fiona
 * @date 2024/12/2 17:21
 */
public class RefreshWeb3Event extends ApplicationEvent {

    public RefreshWeb3Event(Object source) {
        super(source);
    }
}
