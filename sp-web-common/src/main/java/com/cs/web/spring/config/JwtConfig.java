package com.cs.web.spring.config;

import com.cs.web.interceptor.AutoRegInterceptor;
import com.cs.web.interceptor.JwtAuthInterceptor;
import com.cs.web.jwt.JwtHelper;
import com.cs.web.jwt.JwtProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自动注入
 *
 * @author sb
 * @date 2023/5/31 11:09
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({JwtProperties.class})
public class JwtConfig {

    @Bean
    @ConditionalOnProperty(name = "sp.jwt.signKey")
    public JwtHelper jwtHelper(JwtProperties jwtProperties) {
        log.info("jwtHelper init");
        return new JwtHelper(jwtProperties);
    }

    @Bean
    @ConditionalOnBean({JwtHelper.class})
    @ConditionalOnProperty(name = "sp.jwt.autoReg", havingValue = "true", matchIfMissing = true)
    public JwtAuthInterceptor jwtAuthInterceptor(JwtHelper jwtHelper) {
        log.info("JwtAuthInterceptor init");
        return new JwtAuthInterceptor(jwtHelper);
    }

    @Bean
    @ConditionalOnBean(JwtAuthInterceptor.class)
    @ConditionalOnProperty(name = "sp.jwt.autoReg", havingValue = "true", matchIfMissing = true)
    public AutoRegInterceptor jwtInteceptor(JwtAuthInterceptor jwtAuthInterceptor) {
        log.info("AutoRegedInteceptor init jwtAuthInterceptor");
        AutoRegInterceptor inteceptor = new AutoRegInterceptor(jwtAuthInterceptor);
        inteceptor.setOrder(1);
        return inteceptor;
    }
}
