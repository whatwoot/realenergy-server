package com.cs.energy.evm.server.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cs.energy.evm.api.entity.ChainScan;
import com.cs.energy.evm.api.enums.EvmEnum;
import com.cs.energy.evm.server.mapper.ChainScanMapper;
import com.cs.energy.evm.api.service.ChainScanService;
import com.cs.sp.constant.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author gpthk
 * @since 2024-06-03
 */
@Service
public class ChainScanServiceImpl extends ServiceImpl<ChainScanMapper, ChainScan> implements ChainScanService {

    public static final String LIST_KEY = ChainScanServiceImpl.class.getSimpleName() + ":listAndCache";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public List<ChainScan> listBscAndCache() {
        return listBscAndCache(false);
    }

    @Override
    public List<ChainScan> listBscAndCache(boolean force) {
        if (!force) {
            String cache = stringRedisTemplate.opsForValue().get(LIST_KEY);
            if (StringUtils.hasText(cache)) {
                return JSONArray.parseArray(cache, ChainScan.class);
            }
        }

        List<ChainScan> chainScans = getBaseMapper().selectList(new QueryWrapper<ChainScan>().lambda()
                .eq(ChainScan::getChain, EvmEnum.BSC.getChain())
                .eq(ChainScan::getStatus, Constant.ONE_BYTE)
        );
        if(chainScans != null){
            stringRedisTemplate.opsForValue().set(LIST_KEY, JSONArray.toJSONString(chainScans));
        }
        return chainScans;
    }
}
