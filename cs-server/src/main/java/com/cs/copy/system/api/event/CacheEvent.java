package com.cs.copy.system.api.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author fiona
 * @date 2024/9/30 02:58
 */
@Getter
public class CacheEvent extends ApplicationEvent {
    private String category;
    private String cacheKey;
    private Class clazz;
    private Object t;

    public CacheEvent(Object source, String category, String cacheKey, Class clazz, Object t) {
        super(source);
        this.category = category;
        this.cacheKey = cacheKey;
        this.clazz = clazz;
        this.t = t;
    }
}
