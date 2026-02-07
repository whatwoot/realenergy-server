package com.cs.copy.asset.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cs.copy.asset.api.entity.WithdrawFlow;
import com.cs.copy.evm.api.entity.ChainWork;

import java.util.Set;

/**
 * <p>
 * 提现流水 服务类
 * </p>
 *
 * @author gpthk
 * @since 2024-10-03
 */
public interface WithdrawFlowService extends IService<WithdrawFlow> {

    WithdrawFlow add(WithdrawFlow req);

    void updateWithdrawStatus(ChainWork chainWork);

    void checkAndWithdraw(WithdrawFlow withdrawFlow);

    void checkCnyAndWithdraw(WithdrawFlow withdrawFlow);
}
