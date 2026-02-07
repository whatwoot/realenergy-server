package com.cs.web.spring.redismq;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * @authro fun
 * @date 2025/5/23 19:01
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "sp.redismq")
public class RedisMqProperties {
    /**
     * 队列名
     */
    private String streamKey;

    private Consumer consumer = new Consumer();
    private Producer producer = new Producer();

    @Getter
    @Setter
    public static class Producer {
        private String name;
    }

    @Getter
    @Setter
    public static class Consumer {
        private String name;
        /**
         * 消费者组
         */
        private String group;
        /**
         * 重试次数
         */
        private Integer maxRetries = 3;
        /**
         * 阻塞式
         */
        private Duration blockDuration = Duration.ofSeconds(30);
        /**
         * 批量
         */
        private Long batchSize = 500L;
        private long retryBaseMs = 1000;
    }
}
