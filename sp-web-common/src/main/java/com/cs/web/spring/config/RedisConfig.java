package com.cs.web.spring.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.cs.web.aspect.RateLimiterAspect;
import com.cs.web.common.FastJson2JsonRedisSerializer;
import com.cs.web.redis.RedisService;
import com.cs.web.redis.impl.RedisServiceImpl;
import com.cs.web.spring.helper.CacheClient;
import com.cs.web.spring.helper.RedisIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * @author sb
 * @date 2023/8/25 21:04
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(RedisProperties.class)
@AutoConfigureBefore({RedissonAutoConfiguration.class,LockConfig.class})
@ConditionalOnProperty("spring.redis.host")
public class RedisConfig {

    @Bean
    @Primary
    @ConditionalOnProperty(name = "sp.redis.serializer", havingValue = "fast2")
    public RedisTemplate<String, Object> fastRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        log.info("fast2 RedisSerializer init");

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        FastJson2JsonRedisSerializer serializer = new FastJson2JsonRedisSerializer(Object.class);

        // 使用StringRedisSerializer来序列化和反序列化redis的key值
        template.setKeySerializer(RedisSerializer.string());
        template.setHashKeySerializer(RedisSerializer.string());

        // Hash的key也采用StringRedisSerializer的序列化方式
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);

        template.setDefaultSerializer(RedisSerializer.byteArray());

        template.afterPropertiesSet();
        return template;
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = "sp.redis.serializer", havingValue = "jackson2", matchIfMissing = true)
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory, ObjectMapper objectMapper) {
        log.info("jackson2 RedisSerializer init");

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        Jackson2JsonRedisSerializer serializer = new Jackson2JsonRedisSerializer(Object.class);
        serializer.setObjectMapper(objectMapper);
        template.setKeySerializer(RedisSerializer.string());
        template.setHashKeySerializer(RedisSerializer.string());

        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);
        return template;
    }

    @Bean
    @ConditionalOnBean({RedisTemplate.class})
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        log.info("stringRedisTemplate init {}", redisConnectionFactory);
        return new StringRedisTemplate(redisConnectionFactory);
    }

    @Bean
    @ConditionalOnBean({RedisTemplate.class, StringRedisTemplate.class})
    public RedisService redisService(RedisTemplate redisTemplate, StringRedisTemplate stringRedisTemplate) {
        log.info("RedisService init");
        return new RedisServiceImpl(redisTemplate, stringRedisTemplate);
    }

    /**
     * 基于redis的缓存处理类
     *
     * @param stringRedisTemplate
     * @return
     */
    @Bean
    @Primary
    @ConditionalOnBean({StringRedisTemplate.class})
    public CacheClient cacheClient(StringRedisTemplate stringRedisTemplate) {
        log.info("CacheClient init");
        return new CacheClient(stringRedisTemplate);
    }

    /**
     * 基于redis的分布式id生成器
     *
     * @param stringRedisTemplate
     * @return
     */
    @Bean
    @Primary
    @ConditionalOnBean({StringRedisTemplate.class})
    public RedisIdWorker redisIdWorker(StringRedisTemplate stringRedisTemplate) {
        log.info("RedisIdWorker init");
        return new RedisIdWorker(stringRedisTemplate);
    }

    @Bean
    @ConditionalOnMissingBean
    public RateLimiterAspect rateLimiterAspect(RedisTemplate redisTemplate) {
        log.info("RateLimiterAspect init");
        return new RateLimiterAspect(redisTemplate);
    }
}
