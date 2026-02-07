package com.cs.oksdk.config;

import com.cs.oksdk.OkxMultiSdkApi;
import com.cs.oksdk.config.prop.OkxProperties;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @authro fun
 * @date 2025/5/10 14:02
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class OkxHelperConfig {

    @Bean
    @ConfigurationProperties("okx.config")
    public OkxProperties okxProperties() {
        return new OkxProperties();
    }

    @Bean
    public OkxMultiSdkApi okxMultiSdkApi(OkxProperties okxProperties) {
        return new OkxMultiSdkApi(okxProperties.getTestnet());
    }

    @Bean
    public OkxHttpHelper okxHttpHelper() {
        log.info("HttpHelper init");
        OkHttpClient client = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(
                        10,     // 最大空闲连接数（默认5）
                        5,      // 空闲连接存活时间（默认5分钟）
                        TimeUnit.MINUTES
                ))
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(new OkxHttpHelper.LoggingInterceptor())
                .build();
        return new OkxHttpHelper(client);
    }
}
