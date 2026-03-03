package com.cs.energy.system.api.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author fiona
 * @date 2024/12/2 17:21
 */
public class ReFreshEnergyPoolEvent extends ApplicationEvent {

    public ReFreshEnergyPoolEvent(Object source) {
        super(source);
    }
}
