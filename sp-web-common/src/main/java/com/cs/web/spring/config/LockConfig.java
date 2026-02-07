package com.cs.web.spring.config;

import com.cs.web.aspect.DistributedLockAspect;
import com.cs.web.aspect.LocalLockAspect;
import com.cs.web.spring.config.prop.DistributedLockProperties;
import com.cs.web.spring.helper.RedissonLockHelper;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import java.util.Properties;

/**
 * @author sb
 * @date 2021/2/2 04:40
 */
@Configuration
@ConditionalOnProperty(
        prefix = "sp.lock",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
@EnableConfigurationProperties(DistributedLockProperties.class)
@AutoConfigureAfter(RedissonAutoConfiguration.class)
@Slf4j
public class LockConfig {

    public LockConfig() {
        log.info("LockConfig init");
    }

    @Bean
    @ConditionalOnBean(RedissonClient.class)  // 在这里控制
    public DistributedLockAspect distributedLockAspect(RedissonClient redissonClient) {
        return new DistributedLockAspect(redissonClient);
    }

    // 本地锁切面
    @Bean
    @ConditionalOnMissingBean(RedissonClient.class)  // 没有Redisson时创建本地锁
    public LocalLockAspect localLockAspect() {
        return new LocalLockAspect();
    }

    @Bean
    @ConditionalOnBean(RedissonClient.class)
    public RedissonLockHelper redissonLockHelper(RedissonClient redissonClient){
        return new RedissonLockHelper(redissonClient);
    }
}
