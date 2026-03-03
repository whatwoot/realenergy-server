package com.cs.energy.member.server.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cs.energy.global.constants.CacheKey;
import com.cs.energy.member.api.entity.InviteLevel;
import com.cs.energy.member.api.service.InviteLevelService;
import com.cs.energy.member.server.mapper.InviteLevelMapper;
import com.cs.energy.system.api.event.ReBuildCacheEvent;
import com.cs.sp.enums.YesNoByteEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author gpthk
 * @since 2024-12-11
 */
@Slf4j
@Service
public class InviteLevelServiceImpl extends ServiceImpl<InviteLevelMapper, InviteLevel> implements InviteLevelService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @EventListener
    @Async
    public void refresh(ReBuildCacheEvent e) {
        log.info("refresh-cache InviteLevel ALL");
        listAll(true);
    }

    @Override
    public List<InviteLevel> listAll() {
        return listAll(false);
    }

    @Override
    public List<InviteLevel> listAll(boolean force) {
        if (!force) {
            String s = stringRedisTemplate.opsForValue().get(CacheKey.INVITE_LEVEL_ALL);
            if (StringUtils.hasText(s)) {
                return JSONArray.parseArray(s, InviteLevel.class);
            }
        }
        List<InviteLevel> list = list(new QueryWrapper<InviteLevel>().lambda()
                .eq(InviteLevel::getStatus, YesNoByteEnum.YES.getCode())
                .orderByDesc(InviteLevel::getWeight)
        );
        stringRedisTemplate.opsForValue().set(CacheKey.INVITE_LEVEL_ALL, JSONArray.toJSONString(list));
        return list;
    }

    @Override
    public Map<Integer, InviteLevel> listAsMap() {
        return listAll().stream().collect(Collectors.toMap(InviteLevel::getId, item -> item,
                (existing, replacement) -> replacement, LinkedHashMap::new));
    }
}
