package com.cs.energy.member.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cs.energy.asset.api.entity.WithdrawFlow;
import com.cs.energy.member.api.entity.MemberWallet;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author gpthk
 * @since 2025-02-24
 */
public interface MemberWalletService extends IService<MemberWallet> {

    MemberWallet addNewEvmAddr(MemberWallet memberWallet);

    void addLatestWallet(WithdrawFlow withdrawFlow);
}
