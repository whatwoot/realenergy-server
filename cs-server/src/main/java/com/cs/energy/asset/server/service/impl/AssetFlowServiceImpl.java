package com.cs.energy.asset.server.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cs.energy.asset.api.entity.AssetFlow;
import com.cs.energy.asset.api.enums.AssetSceneEnum;
import com.cs.energy.asset.api.enums.AssetTypeEnum;
import com.cs.energy.asset.api.service.AssetFlowService;
import com.cs.energy.asset.api.service.AssetService;
import com.cs.energy.asset.server.mapper.AssetFlowMapper;
import com.cs.energy.global.constants.Gkey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author gpthk
 * @since 2024-09-29
 */
@Slf4j
@Service
public class AssetFlowServiceImpl extends ServiceImpl<AssetFlowMapper, AssetFlow> implements AssetFlowService {

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private AssetService assetService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public BigDecimal sumTotal(Byte type, Long uid, String symbol, Integer day, List<String> scenes) {
        return getBaseMapper().sumTotal(type, uid, symbol, day, scenes);
    }

    public List<AssetFlow>  createMockData(Long uid, Long copyerId, Integer nums){
        List<AssetFlow> list = new ArrayList<>();

        for (int i = 0; i < nums; i++) {
            AssetFlow assetFlow = new AssetFlow();
            assetFlow.setUid(uid);
            assetFlow.setType(AssetTypeEnum.DEFAULT.getCode());
            assetFlow.setSymbol(Gkey.TOKEN);
            assetFlow.setScene(AssetSceneEnum.COPYER.getCode());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("trader", "财富基金_"+i);
            jsonObject.put("type", Arrays.asList("收益", "手续费"));
            jsonObject.put("data", Arrays.asList(100*i+"USDT", -125*i+"CFST"));
            assetFlow.setExtParams(jsonObject.toJSONString());

            assetService.updateAsset(assetFlow);
            list.add(assetFlow);
        }
        return list;

    }
}
