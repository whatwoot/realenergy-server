package com.cs.energy.evm.server.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cs.energy.evm.api.entity.Symbol;
import com.cs.energy.evm.api.service.SymbolService;
import com.cs.energy.evm.server.mapper.SymbolMapper;
import com.cs.energy.global.constants.CacheKey;
import com.cs.energy.system.api.event.ReBuildCacheEvent;
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
 * @since 2024-12-24
 */
@Slf4j
@Service
public class SymbolServiceImpl extends ServiceImpl<SymbolMapper, Symbol> implements SymbolService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @EventListener
    @Async
    public void refreshCache(ReBuildCacheEvent e) {
        log.info("refresh-cache SymbolPrice All");
        listAll(true);
    }

    @Override
    public List<Symbol> listAll() {
        return listAll(false);
    }

    @Override
    public List<Symbol> listAll(boolean force) {
        if (!force) {
            String cache = stringRedisTemplate.opsForValue().get(CacheKey.SYMBOL_ALL);
            if (StringUtils.hasText(cache)) {
                return JSONArray.parseArray(cache, Symbol.class);
            }
        }
        List<Symbol> symbols = list(Wrappers.lambdaQuery(Symbol.class)
                .orderByDesc(Symbol::getWeight));
        if (symbols != null) {
            stringRedisTemplate.opsForValue().set(CacheKey.SYMBOL_ALL, JSONArray.toJSONString(symbols));
        }
        return symbols;
    }

    @Override
    public Map<String, Symbol> listAsTypeChainSymolMap() {
        return listAll().parallelStream().collect(Collectors.toMap(s ->
                String.format("%s:%s:%s", s.getType(), s.getChain(), s.getSymbol()), symbol -> symbol));
    }
}
