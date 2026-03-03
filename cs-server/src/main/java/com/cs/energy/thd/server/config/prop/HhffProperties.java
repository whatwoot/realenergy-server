package com.cs.energy.thd.server.config.prop;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @authro fun
 * @date 2025/3/21 00:56
 */
@Data
@Schema(description = "hhff配置")
@ConfigurationProperties("hhff.config")
public class HhffProperties {
    private String api;
    private Long merchantId;
    private String secret;
}
