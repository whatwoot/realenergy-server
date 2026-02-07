package com.cs.copy.member.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cs.copy.asset.api.entity.WithdrawFlow;
import com.cs.copy.member.api.entity.MemberWallet;

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
