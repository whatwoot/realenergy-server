package com.cs.web.spring.helper;

import com.cs.web.spring.helper.http.HttpHelper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @authro fun
 * @date 2025/5/10 14:02
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class HelperConfig {

    @Bean
    public HttpHelper httpHelper() {
        log.info("HttpHelper init");
        OkHttpClient client = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(
                        10,     // 最大空闲连接数（默认5）
                        5,      // 空闲连接存活时间（默认5分钟）
                        TimeUnit.MINUTES
                ))
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(new HttpHelper.LoggingInterceptor())
                .build();
        return new HttpHelper(client);
    }
}
