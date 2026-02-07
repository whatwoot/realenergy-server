package com.cs.web.spring.redismq;

import cn.hutool.extra.spring.SpringUtil;
import com.cs.web.spring.redismq.base.BaseConsumer;
import com.cs.web.spring.redismq.event.MqMsg;
import com.cs.web.spring.redismq.event.MsgEvent;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 可以使用Spring的消息监听，处理默认消息的消费
 * @EventListener
 * public void onMessage(MsgEvent event){}
 *
 * @authro fun
 * @date 2025/5/24 19:39
 */

public class DefaultRedisMqConsumer extends BaseConsumer<MqMsg> {
    public DefaultRedisMqConsumer(RedisTemplate redisTemplate, RedisMqProperties config) {
        super(redisTemplate, config);
    }

    /**
     * 通过事件派发消息
     * @param record
     * @return
     */
    @Override
    protected boolean process(MapRecord<String, String, String> record) {
        MqMsg mqMsg = parseData(record);
        SpringUtil.publishEvent(new MsgEvent(this, mqMsg));
        return true;
    }

    @Override
    protected Class<MqMsg> getDataType() {
        return MqMsg.class;
    }
}
