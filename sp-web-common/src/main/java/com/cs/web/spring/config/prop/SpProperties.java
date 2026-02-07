package com.cs.web.spring.config.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @authro fun
 * @date 2025/6/20 02:02
 */
@Data
@ConfigurationProperties("sp.config")
public class SpProperties {
    private Boolean signFilter;
    private String SignFilterWhiteUrls;
}
