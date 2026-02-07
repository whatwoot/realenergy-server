package com.cs.copy.system.server.config;

import com.cs.copy.system.server.helper.GeetestHelper;
import com.cs.copy.system.server.helper.prop.GeetestProperties;
import com.cs.web.spring.helper.http.HttpHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @authro fun
 * @date 2025/5/29 21:24
 */
@Slf4j
@Configuration
public class GeetestConfig {

    @Bean(name="androidGeetestProperties")
    @ConfigurationProperties(prefix = "geetest.config.android")
    @ConditionalOnProperty(prefix = "geetest.config.android", name = {"captchaId"})
    public GeetestProperties androidGeetestProperties() {
        return new GeetestProperties();
    }

    @Bean(name = "iosGeetestProperties")
    @ConfigurationProperties(prefix = "geetest.config.ios")
    @ConditionalOnProperty(prefix = "geetest.config.android", name = {"captchaId"})
    public GeetestProperties iosGeetestProperties() {
        return new GeetestProperties();
    }
    @Bean(name = "webGeetestProperties")
    @ConfigurationProperties(prefix = "geetest.config.web")
    @ConditionalOnProperty(prefix = "geetest.config.android", name = {"captchaId"})
    public GeetestProperties webGeetestProperties() {
        return new GeetestProperties();
    }

    @Bean
    @ConditionalOnBean(GeetestProperties.class)
    public GeetestHelper geetestHelper(Map<String, GeetestProperties> propertiesMap, HttpHelper httpHelper) {
        log.info("GeetestHelper init {}", propertiesMap);
        return new GeetestHelper(propertiesMap, httpHelper);
    }
}
