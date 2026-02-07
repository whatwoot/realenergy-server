package com.cs.web.spring.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import java.util.Properties;

/**
 * 全局事务处理切面类
 * 单配置直接放config，包含其他文件时再放到类目文件
 *
 * @author sb
 * @date 2021/2/2 04:40
 */
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(JdbcTransactionManager.class)
@ConditionalOnProperty(value = "sp.advise.tx.enable", havingValue = "true", matchIfMissing = true)
public class TxConfig {

    private static final Logger log = LoggerFactory.getLogger(TxConfig.class);

    /**
     * 创建一个新的切面事务通知
     * bean的名称默认为方法名，此处不使用默认名称transactionInterceptor
     * 目的为了不要覆盖系统默认的注解事务，保持同时生效
     *
     * @param transactionManager
     * @return
     * @see org.springframework.transaction.annotation.ProxyTransactionManagementConfiguration
     */
    @Bean(name = "txAdvice")
    public TransactionInterceptor txAdvice(PlatformTransactionManager transactionManager) {
        log.info("txAdvice bean init");
        Properties properties = new Properties();
        properties.setProperty("get*", "PROPAGATION_NOT_SUPPORTED,-Exception,readOnly");
        properties.setProperty("list*", "PROPAGATION_NOT_SUPPORTED,-Exception,readOnly");
        properties.setProperty("add*", "PROPAGATION_REQUIRED,-Exception");
        properties.setProperty("save*", "PROPAGATION_REQUIRED,-Exception");
        properties.setProperty("insert*", "PROPAGATION_REQUIRED,-Exception");
        properties.setProperty("update*", "PROPAGATION_REQUIRED,-Exception");
        properties.setProperty("edit*", "PROPAGATION_REQUIRED,-Exception");
        properties.setProperty("del*", "PROPAGATION_REQUIRED,-Exception");
        properties.setProperty("delete*", "PROPAGATION_REQUIRED,-Exception");
        properties.setProperty("remove*", "PROPAGATION_REQUIRED,-Exception");
        properties.setProperty("stop*", "PROPAGATION_REQUIRED,-Exception");
        TransactionInterceptor interceptor = new TransactionInterceptor();
        interceptor.setTransactionManager(transactionManager);
        interceptor.setTransactionAttributes(properties);
        return interceptor;
    }

    @Bean
    @ConditionalOnBean(name = "txAdvice")
    public BeanNameAutoProxyCreator txProxy() {
        log.info("txProxy bean init");
        BeanNameAutoProxyCreator creator = new BeanNameAutoProxyCreator();
        creator.setInterceptorNames("txAdvice");
        creator.setBeanNames("*Service", "*ServiceImpl");
        creator.setProxyTargetClass(true);
        creator.setOptimize(true);
        return creator;
    }
}
