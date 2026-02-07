package com.cs.web.spring.config;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * @author sb
 * @date 2023/8/11 16:49
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnBean(MvcConfig.class)
@AutoConfigureAfter(MvcConfig.class)
@Slf4j
public class JacksonConfig implements Jackson2ObjectMapperBuilderCustomizer, Ordered {

    @Bean
    public JavaTimeModule javaTimeModule(){
        return new JavaTimeModule();
    }

    @Override
    public void customize(Jackson2ObjectMapperBuilder builder) {
        log.info("spring-web-common JacksonConfig init");
        builder.featuresToEnable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
        builder.featuresToEnable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        builder.featuresToEnable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS);
    }

    /**
     * 数字越小越先执行，所以后面的服务想要覆盖默认配置，只需要order大于当前即可
     * 因为 Jackson2ObjectMapperBuilderCustomizerConfiguration.StandardJackson2ObjectMapperBuilderCustomizer
     * 默认为0，所以要大于他，才不至于被默认值覆盖
     *
     * @return
     */
    @Override
    public int getOrder() {
        return 1;
    }
}
