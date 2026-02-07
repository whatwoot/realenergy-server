package com.cs.copy.system.server.config;

import com.cs.copy.system.server.helper.TurnstileHelper;
import com.cs.copy.system.server.helper.prop.TurnstileProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author fiona
 * @date 2025/2/21 19:44
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({TurnstileProperties.class})
public class TurnstileConfig {

    @Bean
    @ConditionalOnProperty("cf.config.turnstile.apikey")
    public TurnstileHelper turnstileHelper(TurnstileProperties turnstileProperties) {
        log.info("TurnstileHelper init: {}", turnstileProperties.getApikey());
        return new TurnstileHelper(turnstileProperties);
    }
}
