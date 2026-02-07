package com.cs.copy.evm.server.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cs.copy.evm.api.common.EvmConstant;
import com.cs.copy.evm.api.entity.DecPriceHis;
import com.cs.copy.evm.api.event.DecPriceHisAddEvent;
import com.cs.copy.evm.api.service.DecPriceHisService;
import com.cs.copy.evm.api.service.EvmService;
import com.cs.copy.evm.server.factory.BscChain;
import com.cs.copy.evm.server.factory.EvmFactory;
import com.cs.copy.evm.server.mapper.DecPriceHisMapper;
import com.cs.copy.evm.server.util.SwapPrice;
import com.cs.copy.global.constants.CacheKey;
import com.cs.copy.global.constants.Gkey;
import com.cs.copy.system.server.config.prop.AppProperties;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint112;
import org.web3j.abi.datatypes.generated.Uint32;
import org.web3j.protocol.core.Response;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author gpthk
 * @since 2026-01-04
 */
@Slf4j
@Service
public class DecPriceHisServiceImpl extends ServiceImpl<DecPriceHisMapper, DecPriceHis> implements DecPriceHisService {

    @Autowired
    private AppProperties appProperties;

    private EvmService evmService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private final Cache<String, String> PRICE_CACHE = Caffeine.newBuilder()
            .maximumSize(100)
            .recordStats()
            .build();

    @PostConstruct
    public void init() {
        EvmFactory defaults = EvmFactory.defaults();
        defaults.add("bsc", new BscChain(56L, "https://bsc-dataseed2.bnbchain.org")).asDefault("bsc");
        evmService = new EvmServiceImpl(defaults);

        load2cache();
    }

    private void load2cache() {
        DecPriceHis latestPrice = getBaseMapper().selectOne(Wrappers.lambdaQuery(DecPriceHis.class)
                .eq(DecPriceHis::getSymbol, Gkey.TOKEN)
                .orderByDesc(DecPriceHis::getId)
                .last("limit 1")
        );
        if (latestPrice != null) {
            PRICE_CACHE.put(latestPrice.getSymbol(), String.format("%s,%s", latestPrice.getPriceTime(), latestPrice.getUpdateAt()));
        }
    }


    @Override
    public void scanHis() throws IOException {
        Pair<Response.Error, List<Type>> pair = evmService.ethCall(Gkey.TOKEN_FROM,
                appProperties.getTokenLpAddr(),
                "getReserves", Collections.emptyList(), Arrays.asList(
                        EvmConstant.TYPE_UINT112,
                        EvmConstant.TYPE_UINT112,
                        EvmConstant.TYPE_UINT32
                ));
        if (pair.getLeft() != null) {
            return;
        }
        BigInteger reverse0 = ((Uint112) pair.getRight().get(0)).getValue();
        BigInteger reverse1 = ((Uint112) pair.getRight().get(1)).getValue();
        BigInteger timestamp = ((Uint32) pair.getRight().get(2)).getValue();
        Long priceTime = timestamp.longValue() * Gkey.SECOND_MILLISECOND;
        String lastStr = PRICE_CACHE.getIfPresent(Gkey.TOKEN);
        boolean add = false;
        long now = System.currentTimeMillis();
        if(lastStr == null){
            add = true;
        }else{
            String[] timeArr = lastStr.split(",");
            Long lastPriceTime = Long.parseLong(timeArr[0]);
            Long lastUpdateTime = Long.parseLong(timeArr[1]);
            // 链上时间都变了，直接更新
            if(!lastPriceTime.equals(priceTime)){
                add = true;
            }else if(now - lastUpdateTime > appProperties.getTokenTwapGap()){
                // 如果超过1分钟，也记一条数据
                add = true;
            }
        }
        if (!add) {
            return;
        }
        DecPriceHis decPriceHis = new DecPriceHis();
        decPriceHis.setSymbol(Gkey.TOKEN);
        decPriceHis.setPriceDirection(appProperties.getTokenDirection());
        decPriceHis.setReserve0(reverse0);
        decPriceHis.setDecimals0(18);
        decPriceHis.setDecimals1(18);
        decPriceHis.setReserve1(reverse1);
        decPriceHis.setPriceTime(timestamp.longValue() * Gkey.SECOND_MILLISECOND);
        decPriceHis.setPriceDirection(appProperties.getTokenDirection());
        decPriceHis.setPrice(SwapPrice.getPrice(reverse0, reverse1, 18, 18, appProperties.getTokenDirection()));
        decPriceHis.setUpdateAt(now);
        getBaseMapper().insert(decPriceHis);
        PRICE_CACHE.put(Gkey.TOKEN, String.format("%s,%s", decPriceHis.getPriceTime(), decPriceHis.getUpdateAt()));
        SpringUtil.publishEvent(new DecPriceHisAddEvent(this, decPriceHis));
    }

    @EventListener
    @Async
    public void handleEvent(DecPriceHisAddEvent event) {
        calcPrice();
    }

    public void calcPrice(){
        List<DecPriceHis> sortedList = getBaseMapper().selectList(Wrappers.lambdaQuery(DecPriceHis.class)
                .eq(DecPriceHis::getSymbol, Gkey.TOKEN)
                .ge(DecPriceHis::getPriceTime, System.currentTimeMillis() - (appProperties.getTokenTwapMinute() + 1) * Gkey.MINUTE_MILLISECOND)
                .orderByAsc(DecPriceHis::getPriceTime)
                .orderByAsc(DecPriceHis::getId)
        );
        if(sortedList.size() < appProperties.getTokenTwapMinute()){
            return;
        }
        BigDecimal twapPrice = calcPriceOf(sortedList);
        HashOperations<String, String, String> ops = stringRedisTemplate.opsForHash();
        ops.put(CacheKey.PRICE_MAP, Gkey.TOKEN, twapPrice.setScale(appProperties.getPriceDecimal(), RoundingMode.FLOOR).stripTrailingZeros().toPlainString());
        log.info("Price-{} new {}", Gkey.TOKEN, twapPrice.stripTrailingZeros().toPlainString());
    }


    private BigDecimal calcPriceOf(List<DecPriceHis> sortedList) {
        BigDecimal totalWeightedPrice = BigDecimal.ZERO;
        long totalTimeInterval = 0L;

        // 计算每个时间段内的加权价格
        for (int i = 0; i < sortedList.size() - 1; i++) {
            DecPriceHis current = sortedList.get(i);
            DecPriceHis next = sortedList.get(i + 1);

            // 计算时间间隔（单位：秒或毫秒，保持一致性即可）
            long timeInterval = next.getPriceTime() - current.getPriceTime();

            // 跳过无效的时间间隔
            if (timeInterval <= 0) {
                continue;
            }
            // gap超长的，之前就丢弃
            if(timeInterval > 2 * appProperties.getTokenTwapGap()){
                totalTimeInterval = 0;
                totalWeightedPrice = BigDecimal.ZERO;
                continue;
            }

            // 累加加权价格：价格 × 时间间隔
            totalWeightedPrice = totalWeightedPrice.add(
                    current.getPrice().multiply(BigDecimal.valueOf(timeInterval))
            );
            totalTimeInterval += timeInterval;
        }

        if (totalTimeInterval == 0) {
            // 如果所有时间间隔都为0，返回最后一个价格
            return sortedList.get(sortedList.size() - 1).getPrice();
        }

        // 计算TWAP：总加权价格 ÷ 总时间间隔
        return totalWeightedPrice.divide(
                BigDecimal.valueOf(totalTimeInterval),
                MathContext.DECIMAL128
        );
    }
}
