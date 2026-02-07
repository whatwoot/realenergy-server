package com.cs.web.spring.redismq.base;

import com.alibaba.fastjson2.JSONObject;
import com.cs.web.spring.redismq.RedisMqProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @authro fun
 * @date 2025/5/24 00:08
 */
@Slf4j
public abstract class BaseProducer<T> {
    protected RedisTemplate redisTemplate;
    protected RedisMqProperties config;

    public BaseProducer(RedisTemplate redisTemplate, RedisMqProperties config) {
        this.redisTemplate = redisTemplate;
        this.config = config;
    }

    /**
     * 发送消息到队列
     * @param data
     * @return
     */
    public RecordId send(T data) {
        JSONObject json = JSONObject.from(data);
        log.info("Rs-producer {}, {}, {}",  config.getStreamKey(), config.getProducer().getName(), json.toJSONString());
        return redisTemplate.opsForStream().add(config.getStreamKey(), json);
    }
}
