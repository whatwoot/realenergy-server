package com.cs.web.spring.config;

import com.cs.web.spring.redismq.DefaultRedisMqConsumer;
import com.cs.web.spring.redismq.DefaultRedisMqProducer;
import com.cs.web.spring.redismq.RedisMqProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.PostConstruct;

/**
 * @authro fun
 * @date 2025/5/23 23:33
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(RedisMqProperties.class)
@ConditionalOnProperty(prefix = "sp.redismq", name = "streamKey")
public class RedisMqConfig {

    @PostConstruct
    public void init() {
        log.info("RedisMqConfig init");
    }

    @Bean
    @ConditionalOnProperty(prefix = "sp.redismq.producer", name = "name")
    public DefaultRedisMqProducer redisMqProducer(RedisTemplate redisTemplate,
                                                  RedisMqProperties mqProperties) {
        log.info("DefaultRedisMqProducer init");
        return new DefaultRedisMqProducer(redisTemplate, mqProperties);
    }

    @Bean
    @ConditionalOnProperty(prefix = "sp.redismq.consumer", name = "name")
    public DefaultRedisMqConsumer redisMqConsumer(RedisTemplate redisTemplate,
                                                  RedisMqProperties mqProperties) {
        log.info("DefaultRedisMqConsumer init");
        return new DefaultRedisMqConsumer(redisTemplate, mqProperties);
    }

}
