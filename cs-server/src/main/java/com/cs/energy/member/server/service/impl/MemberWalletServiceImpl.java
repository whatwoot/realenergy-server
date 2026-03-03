package com.cs.energy.member.server.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cs.energy.asset.api.entity.WithdrawFlow;
import com.cs.energy.chain.api.entity.ChainAddress;
import com.cs.energy.chain.api.service.ChainAddressService;
import com.cs.energy.global.constants.CacheKey;
import com.cs.energy.global.constants.Gkey;
import com.cs.energy.member.api.entity.MemberWallet;
import com.cs.energy.member.api.enums.ChainEnum;
import com.cs.energy.member.api.enums.MemberWalletTypeEnum;
import com.cs.energy.member.api.event.AddWalletEvent;
import com.cs.energy.member.api.service.MemberWalletService;
import com.cs.energy.member.server.mapper.MemberWalletMapper;
import com.cs.sp.constant.Constant;
import com.cs.web.spring.helper.CacheClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author gpthk
 * @since 2025-02-24
 */
@Slf4j
@Service
public class MemberWalletServiceImpl extends ServiceImpl<MemberWalletMapper, MemberWallet> implements MemberWalletService {

    @Autowired
    private CacheClient cacheClient;
    @Autowired
    private ChainAddressService chainAddressService;

    @Override
    public void addLatestWallet(WithdrawFlow withdrawFlow) {
        String addr = withdrawFlow.getArriveAddr().toLowerCase();
        MemberWallet memberWallet = getBaseMapper().selectOne(new QueryWrapper<MemberWallet>().lambda()
                .eq(MemberWallet::getChain, withdrawFlow.getChain())
                .eq(MemberWallet::getWallet, addr)
                .eq(MemberWallet::getUid, withdrawFlow.getUid())
        );
        // 把其他提现钱包降低权重
        MemberWallet updateWeight = new MemberWallet();
        updateWeight.setWeight(Constant.ZERO_INT);
        LambdaQueryWrapper<MemberWallet> queryWrapper = new QueryWrapper<MemberWallet>().lambda()
                .eq(MemberWallet::getType, MemberWalletTypeEnum.WITHDRAW.getCode())
                .eq(MemberWallet::getUid, withdrawFlow.getUid());
        if (memberWallet != null) {
            queryWrapper.ne(MemberWallet::getId, memberWallet.getId());
        }
        int row = getBaseMapper().update(updateWeight, queryWrapper);
        log.info("AddLatestWallet downgrade {}", row);
        if (memberWallet == null) {
            Long seq = getBaseMapper().selectCount(new QueryWrapper<MemberWallet>().lambda()
                    .eq(MemberWallet::getUid, withdrawFlow.getUid())
                    .eq(MemberWallet::getType, MemberWalletTypeEnum.WITHDRAW.getCode())
            );
            MemberWallet newLatest = new MemberWallet();
            newLatest.setUid(withdrawFlow.getUid());
            newLatest.setChain(withdrawFlow.getChain());
            newLatest.setType(MemberWalletTypeEnum.WITHDRAW.getCode());
            newLatest.setCreateAt(System.currentTimeMillis());
            newLatest.setWeight(Constant.ONE_INT);
            newLatest.setSeq(seq.intValue());
            getBaseMapper().insert(newLatest);
        } else {
            // 不是1就升到1
            if (!Constant.ONE_INT.equals(memberWallet.getWeight())) {
                updateWeight = new MemberWallet();
                updateWeight.setId(memberWallet.getId());
                updateWeight.setWeight(Constant.ONE_INT);
                getBaseMapper().updateById(updateWeight);
            }
        }
    }

    @Override
    public MemberWallet addNewEvmAddr(MemberWallet memberWallet) {
        return cacheClient.withLock(CacheKey.GEN_WALLET_LOCK + memberWallet.getUid(), Gkey.TEN, () -> {
            MemberWallet exists = getBaseMapper().selectOne(new QueryWrapper<MemberWallet>().lambda()
                    .eq(MemberWallet::getType, MemberWalletTypeEnum.RECHARGE.getCode())
                    .eq(MemberWallet::getUid, memberWallet.getUid())
            );
            if (exists != null) {
                return exists;
            }
            memberWallet.setType(MemberWalletTypeEnum.RECHARGE.getCode());
            memberWallet.setChain(ChainEnum.BSC.getCode());
            ChainAddress chainAddress = chainAddressService.genWallet();
            memberWallet.setWallet(chainAddress.getAddr());
            memberWallet.setCreateAt(System.currentTimeMillis());
            getBaseMapper().insert(memberWallet);
            SpringUtil.publishEvent(new AddWalletEvent(this, memberWallet));
            return memberWallet;
        });
    }
}
