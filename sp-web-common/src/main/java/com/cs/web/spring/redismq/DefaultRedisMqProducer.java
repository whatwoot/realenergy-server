package com.cs.web.spring.redismq;

import com.cs.web.spring.redismq.base.BaseProducer;
import com.cs.web.spring.redismq.event.MqMsg;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @authro fun
 * @date 2025/5/24 19:39
 */

public class DefaultRedisMqProducer extends BaseProducer<MqMsg> {
    public DefaultRedisMqProducer(RedisTemplate redisTemplate, RedisMqProperties config) {
        super(redisTemplate, config);
    }
}
