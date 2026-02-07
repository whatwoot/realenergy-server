package com.cs.copy.system.server.helper.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author fiona
 * @date 2025/2/21 19:44
 */
@Data
@ConfigurationProperties("cf.config.turnstile")
public class TurnstileProperties {
    private String apikey;
    private String secret;
    private String verifyUrl;
}
