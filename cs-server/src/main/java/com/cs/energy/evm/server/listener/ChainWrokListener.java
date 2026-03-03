package com.cs.energy.evm.server.listener;

import cn.hutool.core.util.StrUtil;
import com.cs.energy.asset.api.service.AssetService;
import com.cs.energy.asset.api.service.WithdrawFlowService;
import com.cs.energy.evm.api.entity.ChainWork;
import com.cs.energy.evm.api.enums.ChainWorkTypeEnum;
import com.cs.energy.evm.api.event.ChainWorkConfirmEvent;
import com.cs.energy.evm.server.queue.GasWorkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * @author fiona
 * @date 2024/12/21 04:46
 */
@Slf4j
@Component
public class ChainWrokListener {

    @Autowired
    private AssetService assetService;

    @Autowired
    private WithdrawFlowService withdrawFlowService;

    @Autowired
    private GasWorkService gasWorkService;

    /**
     * 提现确认
     *
     * @param e
     */
    @TransactionalEventListener
    @Async
    public void withdrawConfirm(ChainWorkConfirmEvent e) {
        ChainWork chainWork = e.getChainWork();
        if (ChainWorkTypeEnum.WITHDRAW.eq(chainWork.getType())) {
            try {
                // 兼容旧版
                withdrawFlowService.updateWithdrawStatus(chainWork);
            } catch (Exception ex) {
                log.info(StrUtil.format("WITHDRAW-PROCESS fail {}:{}", chainWork.getId(), chainWork.getHash()), e);
            }
        }
    }

    /**
     * 充值确认
     *
     * @param e
     */
    @TransactionalEventListener
    @Async
    public void deposit(ChainWorkConfirmEvent e) {
        ChainWork chainWork = e.getChainWork();
        if (ChainWorkTypeEnum.DEPOSIT.eq(chainWork.getType())) {
            try {
//                assetService.addDeposit(chainWork);
                assetService.addDepositByCa(chainWork);
            } catch (Exception ex) {
                log.info(StrUtil.format("WITHDRAW-PROCESS fail {}:{}", chainWork.getId(), chainWork.getHash()), e);
            }
        }
    }

    /**
     * gas确认
     *
     * @param e
     */
    @TransactionalEventListener
    @Async
    public void gasReached(ChainWorkConfirmEvent e) {
        ChainWork chainWork = e.getChainWork();
        if (ChainWorkTypeEnum.GAS.eq(chainWork.getType())) {
            try {
                gasWorkService.updateCollectGoon(chainWork);
            } catch (Exception ex) {
                log.info(StrUtil.format("WITHDRAW-PROCESS fail {}:{}", chainWork.getId(), chainWork.getHash()), e);
            }
        }
    }
}
