package com.cs.copy.evm.server.config;

import com.cs.copy.evm.server.config.prop.EvmProperties;
import com.cs.copy.evm.server.factory.EvmFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author fiona
 * @date 2024/5/29 20:06
 */
@Configuration(proxyBeanMethods = false)
@Slf4j
@EnableConfigurationProperties({EvmProperties.class})
public class EvmConfig {

    /**
     * 改为由数据库提供factory实现
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public EvmFactory evmFactory() {
        log.info("init EvmFactory");
        EvmFactory evmFactory = EvmFactory.defaults();
        return evmFactory;
    }
}
