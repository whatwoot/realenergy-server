package com.cs.energy.evm.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cs.energy.evm.api.entity.ChainWork;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author gpthk
 * @since 2024-12-21
 */
public interface ChainWorkService extends IService<ChainWork> {
    void updateStake(ChainWork chainWork);
    void updateUnStake(ChainWork chainWork);
}
