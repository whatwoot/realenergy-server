package com.cs.energy.system.server.config;

import com.cs.energy.system.server.config.prop.AppProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author fiona
 * @date 2024/11/5 00:03
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({AppProperties.class})
public class AppConfig {

}
