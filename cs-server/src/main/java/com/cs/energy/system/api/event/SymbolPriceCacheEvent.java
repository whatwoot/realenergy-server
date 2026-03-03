package com.cs.energy.system.api.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author fiona
 * @date 2025/2/20 23:29
 */
@Getter
public class SymbolPriceCacheEvent extends ApplicationEvent {
    public SymbolPriceCacheEvent(Object source) {
        super(source);
    }
}
