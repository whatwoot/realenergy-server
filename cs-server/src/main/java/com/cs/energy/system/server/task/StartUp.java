package com.cs.energy.system.server.task;

import cn.hutool.extra.spring.SpringUtil;
import com.cs.energy.global.constants.CacheKey;
import com.cs.energy.member.api.service.MemberService;
import com.cs.energy.system.api.enums.SystemStatusEnum;
import com.cs.energy.system.api.event.ReBuildCacheEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author fiona
 * @date 2024/9/29 07:06
 */
@Slf4j
@Component
public class StartUp implements ApplicationListener<ApplicationStartedEvent> {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private MemberService memberService;

    /**
     * 启动事件
     * @param event
     */
    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        SpringUtil.publishEvent(new ReBuildCacheEvent(this));
        stringRedisTemplate.opsForValue().set(CacheKey.GAME_STAUS, SystemStatusEnum.OK.getCode());
    }
}
