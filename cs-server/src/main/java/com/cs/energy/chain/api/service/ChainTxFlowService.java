package com.cs.energy.chain.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cs.energy.chain.api.entity.ChainTxFlow;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author gpthk
 * @since 2024-10-24
 */
public interface ChainTxFlowService extends IService<ChainTxFlow> {

    int addDeposits(ChainTxFlow flow);
}
