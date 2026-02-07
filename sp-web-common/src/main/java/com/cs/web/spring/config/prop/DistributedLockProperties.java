package com.cs.web.spring.config.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 分布式锁配置属性
 */
@Data
@ConfigurationProperties(prefix = "sp.lock")
public class DistributedLockProperties {
    
    /**
     * 是否启用分布式锁
     */
    private boolean enabled = true;
    
    /**
     * 默认锁前缀
     */
    private String defaultPrefix = "__:lock:";
    
    /**
     * 默认失败消息
     */
    private String defaultMessage = "sp.common.frequency";
}