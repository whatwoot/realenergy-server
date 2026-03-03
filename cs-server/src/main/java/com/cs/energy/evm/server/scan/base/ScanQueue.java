package com.cs.energy.evm.server.scan.base;

import cn.hutool.core.lang.Assert;
import cn.hutool.extra.spring.SpringUtil;
import com.cs.energy.chain.api.enums.ChainTxFlowStatusEnum;
import com.cs.energy.evm.api.service.EvmService;
import com.cs.energy.chain.api.entity.ChainTxFlow;
import com.cs.energy.chain.api.service.ChainTxFlowService;
import com.cs.sp.queue.DelayedVo;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author fiona
 * @date 2024/6/3 16:54
 */
public interface ScanQueue {

    String ukey();

    boolean accept(ChainTxFlow txFlow);

    /**
     * 链上事务记录队列
     *
     * @return
     */
    LinkedBlockingDeque<ChainTxFlow> getQueue();

    /**
     * 事务账本确认队列
     *
     * @return
     */
    DelayQueue<DelayedVo<ChainTxFlow>> getDelayQueue();

    /**
     * 确认后任务队列
     *
     * @return
     */
    LinkedBlockingDeque<ChainTxFlow> getConfirmQueue();

    /**
     * 扫块结果任务
     *
     * @param take
     * @return
     */
    boolean apply(ChainTxFlow take);

    /**
     * 扫块确认后任务
     *
     * @param take
     * @return
     */
    boolean confirm(ChainTxFlow take);

    /**
     * 检查账单失败
     *
     * @param take 链上扫块事务
     * @param e    异常
     * @return true 阻止默认
     * false 不阻止默认
     */
    boolean onCheckReceiptFail(ChainTxFlow take, Exception e);

    void onTaskFail(Exception e);

    void onDelayTaskFail(Exception e);

    void onConfirmFail(Exception e);

    int maxCheckNum();

    /**
     * 扫块结果添加到延时
     *
     * @param txFlow
     */
    default void add(ChainTxFlow txFlow) {
        getQueue().add(txFlow);
    }

    default void addConfirm(ChainTxFlow txFlow){
        getConfirmQueue().add(txFlow);
    }


    /**
     * 处理扫块结果
     */
    default void runTask() {
        ChainTxFlow take;
        while (!Thread.currentThread().isInterrupted()) {
            try {
                take = getQueue().take();
                if (take != null) {
                    // 处理完成后，加入到延时确认队列
                    if (apply(take)) {
                        getDelayQueue().add(new DelayedVo<>(take, take.getConfirmAt()));
                    }
                }
            } catch (Exception e) {
                onTaskFail(e);
            }
        }
    }

    default void runDelayCheckTask() {
        ChainTxFlow take;
        while (!Thread.currentThread().isInterrupted()) {
            try {
                DelayedVo<ChainTxFlow> delayedVo = getDelayQueue().take();
                if (delayedVo != null) {
                    take = delayedVo.getData();
                    checkReceipt(take);
                }
            } catch (Exception e) {
                onDelayTaskFail(e);
            }
        }
    }

    default void runConfirmTask() {
        ChainTxFlow take = null;
        while (!Thread.currentThread().isInterrupted()) {
            try {
                take = null;
                take = getConfirmQueue().take();
                if (take != null) {
                    confirm(take);
                }
            } catch (Exception e) {
                if (take != null) {
                    getConfirmQueue().addFirst(take);
                }
                onConfirmFail(e);
            }
        }
    }

    /**
     * 根据链上事务记录获取事务
     *
     * @param take
     * @return
     */
    default TransactionReceipt getTxReceipt(ChainTxFlow take) {
        TransactionReceipt transactionReceipt = null;
        try {
            EvmService evmService = SpringUtil.getBean(EvmService.class);
            EthGetTransactionReceipt send = evmService.web3j().ethGetTransactionReceipt(take.getHash()).send();
            if (send.getTransactionReceipt().isPresent()) {
                transactionReceipt = send.getTransactionReceipt().get();
            }
        } catch (IOException e) {
        }
        Assert.notNull(transactionReceipt);
        return transactionReceipt;
    }

    /**
     * 默认检查链上事务的方法，可以覆盖重写
     *
     * @param take
     */
    default void checkReceipt(ChainTxFlow take) {
        ChainTxFlow update = new ChainTxFlow();
        try {
            update.setId(take.getId());
            TransactionReceipt transactionReceipt = getTxReceipt(take);
            String status = transactionReceipt.getStatus();
            BigInteger statusBig = Numeric.toBigInt(status);
            update.setStatus(ChainTxFlowStatusEnum.CONFIRMED.getCode());
            update.setReceiptStatus(statusBig.byteValue());
            SpringUtil.getBean(ChainTxFlowService.class).updateById(update);
            // 更新结果
            take.setStatus(update.getStatus());
            take.setReceiptStatus(update.getReceiptStatus());
            getConfirmQueue().add(take);
        } catch (Exception e) {
            boolean preventDefault = onCheckReceiptFail(take, e);
            // 如果需要阻止默认行为，则返回false
            if (!preventDefault) {
                if (take.getErrorNum() >= maxCheckNum() - 1) {
                    update.setErrorNum(take.getErrorNum() + 1);
                    update.setStatus(ChainTxFlowStatusEnum.FAIL.getCode());
                    SpringUtil.getBean(ChainTxFlowService.class).updateById(update);
                } else {
                    update.setErrorNum(take.getErrorNum() + 1);
                    SpringUtil.getBean(ChainTxFlowService.class).updateById(update);
                }
            }
        }
    }
}
