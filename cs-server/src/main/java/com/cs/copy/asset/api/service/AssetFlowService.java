package com.cs.copy.asset.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cs.copy.asset.api.entity.AssetFlow;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author gpthk
 * @since 2024-09-29
 */
public interface AssetFlowService extends IService<AssetFlow> {

    BigDecimal sumTotal(Byte type, Long uid, String symbol, Integer day,  List<String> scenes);
    List<AssetFlow>  createMockData(Long uid, Long copyerId, Integer nums);

}
