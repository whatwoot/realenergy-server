package com.cs.copy.member.server.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cs.copy.global.constants.CacheKey;
import com.cs.copy.member.api.entity.TeamLevel;
import com.cs.copy.member.api.service.TeamLevelService;
import com.cs.copy.member.server.mapper.TeamLevelMapper;
import com.cs.copy.system.api.event.ReBuildCacheEvent;
import com.cs.sp.enums.YesNoByteEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
public class TeamLevelServiceImpl extends ServiceImpl<TeamLevelMapper, TeamLevel> implements TeamLevelService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @EventListener
    @Async
    public void refresh(ReBuildCacheEvent e) {
        listAll(true);
        log.info("refresh-cache TeamLevel All");
    }

    @Override
    public List<TeamLevel> listAll() {
        return listAll(false);
    }

    @Override
    public List<TeamLevel> listAll(boolean force) {
        if (!force) {
            String s = stringRedisTemplate.opsForValue().get(CacheKey.TEAM_LEVEL_ALL);
            if (StringUtils.hasText(s)) {
                return JSONArray.parseArray(s, TeamLevel.class);
            }
        }
        List<TeamLevel> list = list(new QueryWrapper<TeamLevel>().lambda()
                .eq(TeamLevel::getStatus, YesNoByteEnum.YES.getCode())
                .orderByDesc(TeamLevel::getWeight)
        );
        stringRedisTemplate.opsForValue().set(CacheKey.TEAM_LEVEL_ALL, JSONArray.toJSONString(list));
        return list;
    }

    @Override
    public Map<Integer, TeamLevel> listAsMap() {
        return listAll().stream().collect(Collectors.toMap(TeamLevel::getId, item -> item));
    }
}
