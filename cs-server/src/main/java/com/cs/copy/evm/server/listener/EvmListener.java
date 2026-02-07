package com.cs.copy.evm.server.listener;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.cs.copy.asset.api.service.WithdrawFlowService;
import com.cs.sp.common.BeanCopior;
import com.cs.sp.enums.YesNoByteEnum;
import com.cs.web.spring.helper.tgbot.TgBotHelper;
import com.cs.web.spring.helper.tgbot.dto.TgNotifyDTO;
import com.cs.web.spring.helper.tgbot.event.TgNotifyEvent;
import com.cs.copy.asset.api.entity.WithdrawFlow;
import com.cs.copy.asset.api.event.WithdrawEvent;
import com.cs.copy.asset.api.vo.WithdrawFlowVO;
import com.cs.copy.evm.api.entity.ChainWork;
import com.cs.copy.evm.server.queue.WithdrawWorkQueueService;
import com.cs.copy.member.api.enums.ChainEnum;
import com.cs.copy.member.api.service.MemberWalletService;
import com.cs.copy.system.api.enums.SseNameEnum;
import com.cs.copy.system.api.service.SseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * @author fiona
 * @date 2024/12/16 16:35
 */
@Slf4j
@Component
public class EvmListener {

    @Autowired
    private SseService sseService;

    @Autowired
    private WithdrawFlowService withdrawFlowService;

    @Autowired
    private MemberWalletService memberWalletService;

    @Autowired
    private TgBotHelper tgBotHelper;

    @Autowired
    private Environment env;

    @TransactionalEventListener
    @Async
    public void tgNotify(WithdrawEvent e) {
        WithdrawFlow withdrawFlow = e.getWithdrawFlow();
        if (YesNoByteEnum.NO.eq(withdrawFlow.getAuditStatus())) {
            TgNotifyDTO tgNotifyDTO = new TgNotifyDTO();
            tgNotifyDTO.setScene("\uD83D\uDCB0 新的提现通知 \uD83D\uDCB0");
            tgNotifyDTO.setOriented("withdraw");
            tgNotifyDTO.setMember(StrUtil.format("用户：{} ", withdrawFlow.getUid()));
            tgNotifyDTO.setThings(StrUtil.format("数量：{} {}", withdrawFlow.getQuantity().stripTrailingZeros().toPlainString(),
                    withdrawFlow.getSymbol()));
            tgNotifyDTO.setTx("记录ID：" + withdrawFlow.getId());
            tgNotifyDTO.setCreateAt(withdrawFlow.getCreateAt());
            SpringUtil.publishEvent(new TgNotifyEvent(this, tgNotifyDTO));
        }
    }

    /**
     * tg通知
     *
     * @param e
     */
    @TransactionalEventListener
    @Async
    public void addEvm(WithdrawEvent e) {
        WithdrawFlow withdrawFlow = e.getWithdrawFlow();
        if (!ChainEnum.BSC.eq(withdrawFlow.getChain())) {
            return;
        }
        withdrawFlowService.checkAndWithdraw(withdrawFlow);
    }


    @TransactionalEventListener
    @Async
    public void addWithdrawWallet(WithdrawEvent e) {
        WithdrawFlow withdrawFlow = e.getWithdrawFlow();
        if (!ChainEnum.BSC.eq(withdrawFlow.getChain())) {
            return;
        }
        try {
//            memberWalletService.addLatestWallet(withdrawFlow);
        } catch (Throwable ex) {
            log.warn(StrUtil.format("AddLatest-wallet failed, {}, {}", withdrawFlow.getUid(), withdrawFlow.getArriveAddr()), ex);
        }
    }
}
