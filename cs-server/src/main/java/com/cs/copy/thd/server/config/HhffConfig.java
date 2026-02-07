package com.cs.copy.thd.server.config;

import com.cs.copy.thd.server.config.prop.HhffProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @authro fun
 * @date 2025/3/21 00:57
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(HhffProperties.class)
public class HhffConfig {


    @Bean
    public HhffHelper hhffHelper(HhffProperties hhffProperties) {
        log.info("HhffHelper init {}", hhffProperties.getMerchantId());
        return new HhffHelper(hhffProperties);
    }

}
