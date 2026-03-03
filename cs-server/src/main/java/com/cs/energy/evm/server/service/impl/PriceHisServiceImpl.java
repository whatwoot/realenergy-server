package com.cs.energy.evm.server.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cs.energy.evm.api.common.EvmConstant;
import com.cs.energy.evm.api.entity.PriceHis;
import com.cs.energy.evm.api.service.EvmService;
import com.cs.energy.evm.api.service.PriceHisService;
import com.cs.energy.evm.server.mapper.PriceHisMapper;
import com.cs.energy.global.constants.Gkey;
import com.cs.oksdk.constant.CopyCacheKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint112;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint32;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.Response;
import org.web3j.protocol.core.methods.response.EthBlock;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

import static com.cs.sp.common.WebAssert.expectNotNull;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author gpthk
 * @since 2024-12-10
 */
@Slf4j
@Service
public class PriceHisServiceImpl extends ServiceImpl<PriceHisMapper, PriceHis> implements PriceHisService {

    @Autowired
    private EvmService evmService;

    @Autowired
    private PriceHisMapper priceHisMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public BigDecimal twap(String token) {
        String price = stringRedisTemplate.opsForValue().get(CopyCacheKey.PRICE+token.toLowerCase());
        expectNotNull(price,"chk.pricehis.invaildPrice");
        return new BigDecimal(price);
    }

    /**
     * 每分钟执行一次价格收集,但只保存5分钟的记录，如果第一次采集成功，则后续4次不再重复采集
     * 每次重新计算最新30分钟加权平均价格，存入cache中
     */
//    @Scheduled(cron = "0 * * * * ?")
    public void priceCllect(){

        BigInteger price0CumulativeLast = this.price0cumulativelast(EvmConstant.BSC_CHAIN, Gkey.TOKEN_PAIR);
        if(price0CumulativeLast.compareTo(BigInteger.ZERO)<=0){
            return;
        }

        BigInteger price1CumulativeLast = this.price1cumulativelast(EvmConstant.BSC_CHAIN, Gkey.TOKEN_PAIR);
        if(price1CumulativeLast.compareTo(BigInteger.ZERO)<=0){
            return;
        }

        List<BigInteger> reserves = this.getReserves(EvmConstant.BSC_CHAIN, Gkey.TOKEN_PAIR);
        if(reserves==null || reserves.size()<3) {
            return;
        }
        BigInteger reserve0 = reserves.get(0);
        BigInteger reserve1 = reserves.get(1);
        BigInteger lastBlockTimestamp = reserves.get(2);

        // 获得最近30分钟的价格记录
        List<PriceHis> priceHises = priceHisMapper.selectList(new QueryWrapper<PriceHis>().lambda()
                .gt(PriceHis::getCreateAt, System.currentTimeMillis() - Gkey.PRICE_TIME_WINDOW - Gkey.FIVE_MINUTE_MILLISECOND)
                .orderByDesc(PriceHis::getCreateAt)
        );

        //每5分钟记录一次数据，检查最新的一条记录是否是当前5分钟周期内的
        if(priceHises==null || priceHises.isEmpty() || priceHises.get(0).getCreateAt() < System.currentTimeMillis() / Gkey.FIVE_MINUTE_MILLISECOND * Gkey.FIVE_MINUTE_MILLISECOND){
            PriceHis newPriceHis = new PriceHis();
            newPriceHis.setSymbol(Gkey.TOKEN);
            newPriceHis.setPrice0CumulativeLast(new BigDecimal(price0CumulativeLast));
            newPriceHis.setPrice1CumulativeLast(new BigDecimal(price1CumulativeLast));
            newPriceHis.setBlockTimestampLast(reserves.get(2));
            newPriceHis.setCreateAt(System.currentTimeMillis());

            priceHisMapper.insert(newPriceHis);
            log.info("price his collect success: {}", newPriceHis.getId());
        }

        //计算最新价格
        if(priceHises!=null && !priceHises.isEmpty()){

            // 获取最新区块的 timestamp
            try {
                EthBlock ethBlock = evmService.web3j().ethGetBlockByNumber(DefaultBlockParameterName.LATEST, false).send();
                if (ethBlock != null && ethBlock.getBlock() != null) {
                    BigInteger timestamp = ethBlock.getBlock().getTimestamp();
                    //如果最新区块时间跟最新的价格记录时间不一致，则把从上次记录时间以来的价格也累计上去
                    if(lastBlockTimestamp.compareTo(timestamp)<0){
                        BigInteger timeElapsed = timestamp.subtract(lastBlockTimestamp);
                        price0CumulativeLast = price0CumulativeLast.add(reserve1.shiftLeft(112).divide(reserve0).multiply(timeElapsed));
                        price1CumulativeLast = price1CumulativeLast.add(reserve0.shiftLeft(112).divide(reserve1).multiply(timeElapsed));
                        lastBlockTimestamp = timestamp;
                    }
                }
            }catch (Exception e){
                log.warn("获取最新区块 timestamp 失败", e);
                return;
            }

            //使用当前查询出来的历史价格记录中，最早的一条
            PriceHis lastPriceHis = priceHises.get(priceHises.size()-1);
            BigInteger timeElapsed = lastBlockTimestamp.subtract(lastPriceHis.getBlockTimestampLast());

            if(timeElapsed.compareTo(BigInteger.ZERO)>0){
                BigDecimal price0 = new BigDecimal(price0CumulativeLast).subtract(lastPriceHis.getPrice0CumulativeLast())
                        .divide(new BigDecimal(timeElapsed), 36, RoundingMode.FLOOR)
                        .divide(new BigDecimal("2").pow(112), 18, RoundingMode.HALF_UP);
                BigDecimal price1 = new BigDecimal(price1CumulativeLast).subtract(lastPriceHis.getPrice1CumulativeLast())
                        .divide(new BigDecimal(timeElapsed), 36, RoundingMode.HALF_UP)
                        .divide(new BigDecimal("2").pow(112), 18, RoundingMode.HALF_UP);
                stringRedisTemplate.opsForValue().set(CopyCacheKey.PRICE+Gkey.TOKEN_0.toLowerCase(), price0.toString());
                stringRedisTemplate.opsForValue().set(CopyCacheKey.PRICE+Gkey.TOKEN_1.toLowerCase(), price1.toString());

//                log.info("price update success: token0 price={}, token1 price={}", price0.toPlainString(), price1.toPlainString());
            }
        }


    }

    private BigInteger price0cumulativelast(String chain, String pair){
        Pair<Response.Error, List<Type>> pairRes = null;
        try {
            pairRes = evmService.ethCall(chain,Gkey.TOKEN_FROM, pair,
                    EvmConstant.GET_PRICE0CUMULATIVELAST, Arrays.asList(), Arrays.asList(EvmConstant.TYPE_UINT256));
            if (pairRes.getLeft() != null) {
                log.info("price1cumulativelast {} fail", pair);
                return BigInteger.ZERO;
            }
        } catch (Exception e) {
            log.warn(StrUtil.format("price1cumulativelast {} fail", pair), e);
            return BigInteger.ZERO;
        }

        List<Type> res = pairRes.getRight();
        return ((Uint256) res.get(0)).getValue();
    }

    private BigInteger price1cumulativelast(String chain, String pair){
        Pair<Response.Error, List<Type>> pairRes = null;
        try {
            pairRes = evmService.ethCall(chain,Gkey.TOKEN_FROM, pair,
                    EvmConstant.GET_PRICE1CUMULATIVELAST, Arrays.asList(), Arrays.asList(EvmConstant.TYPE_UINT256));
            if (pairRes.getLeft() != null) {
                log.info("price1cumulativelast {} fail", pair);
                return BigInteger.ZERO;
            }
        } catch (Exception e) {
            log.warn(StrUtil.format("price1cumulativelast {} fail", pair), e);
            return BigInteger.ZERO;
        }

        List<Type> res = pairRes.getRight();
        return ((Uint256) res.get(0)).getValue();
    }

    private List<BigInteger> getReserves(String chain, String pair){
        Pair<Response.Error, List<Type>> pairRes = null;
        try {
            pairRes = evmService.ethCall(chain,Gkey.TOKEN_FROM, pair,
                    EvmConstant.GET_RESERVES, Arrays.asList(), Arrays.asList(EvmConstant.TYPE_UINT112, EvmConstant.TYPE_UINT112, EvmConstant.TYPE_UINT32));
            if (pairRes.getLeft() != null) {
                log.info("getReserves {} fail", pair);
                return null;
            }
        } catch (Exception e) {
            log.warn(StrUtil.format("getReserves {} fail", pair), e);
            return null;
        }

        List<Type> res = pairRes.getRight();
        BigInteger reserve0 = ((Uint112) res.get(0)).getValue();
        BigInteger reserve1 = ((Uint112) res.get(1)).getValue();
        BigInteger blockTimestampLast = ((Uint32) res.get(2)).getValue();
        return Arrays.asList(reserve0, reserve1,blockTimestampLast);
    }
}
