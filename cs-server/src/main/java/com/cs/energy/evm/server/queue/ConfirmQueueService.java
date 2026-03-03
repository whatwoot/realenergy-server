package com.cs.energy.evm.server.queue;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cs.energy.asset.api.service.AssetService;
import com.cs.energy.asset.api.service.WithdrawFlowService;
import com.cs.energy.evm.api.entity.ChainWork;
import com.cs.energy.evm.api.enums.ChainWorkCollectedEnum;
import com.cs.energy.evm.api.enums.ChainWorkTxStatusEnum;
import com.cs.energy.evm.api.enums.ChainWorkTypeEnum;
import com.cs.energy.evm.api.event.ChainWorkConfirmEvent;
import com.cs.energy.evm.api.service.ChainWorkService;
import com.cs.energy.evm.api.service.EvmService;
import com.cs.energy.evm.server.mapper.ChainWorkMapper;
import com.cs.energy.global.constants.Gkey;
import com.cs.energy.member.api.enums.ChainEnum;
import com.cs.sp.enums.YesNoByteEnum;
import com.cs.web.util.BeanCopior;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.List;

/**
 * 只需要确认到账的业务
 * eg: 充值
 * eg: gas转账
 *
 * @author fiona
 * @date 2024/12/21 02:23
 */
@Slf4j
@Component
public class ConfirmQueueService {

    @Autowired
    private ChainWorkMapper chainWorkMapper;

    @Autowired
    private EvmService evmService;

    @Autowired
    private AssetService assetService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private WithdrawFlowService withdrawFlowService;

    @Autowired
    private GasWorkService gasWorkService;

    @Autowired
    private CollectWorkService collectWorkService;

    @Autowired
    private ChainWorkService chainWorkService;

    /**
     * 集中在一起做确认
     * 区块确认
     */
    //@Scheduled(fixedRate = 6000L, initialDelay = 1000L)
    public void confirm() {
        List<ChainWork> chainWorks = chainWorkMapper.selectList(new QueryWrapper<ChainWork>().lambda()
                .eq(ChainWork::getChain, ChainEnum.BSC.getCode())
                .eq(ChainWork::getStatus, YesNoByteEnum.YES.getCode())
                .eq(ChainWork::getTxStatus, ChainWorkTxStatusEnum.CONFIRMING.getCode())
                .le(ChainWork::getConfirmAt, System.currentTimeMillis())
        );
        for (ChainWork chainWork : chainWorks) {
            try {
                EthGetTransactionReceipt receiptReq = evmService.web3j().ethGetTransactionReceipt(chainWork.getHash()).send();
                transactionTemplate.execute(tx -> {
                    // TODO: 这里会可能出现 receiptReq.getTransactionReceipt().isPresent()为false的情况
                    //      此时会触发不停的检查，但是因为各种原因，不能直接认定为失败，去触发重试，因为有多种情况可能
                    //      比如节点同步异常，比如低gas卡住，等等原因
                    TransactionReceipt receipt = receiptReq.getTransactionReceipt().get();
                    String status = receipt.getStatus();
                    log.info("receipt gas: used {}, price: {}, calc: {}", receipt.getGasUsed(), Numeric.decodeQuantity(receipt.getEffectiveGasPrice()), receipt.getCumulativeGasUsed());
                    BigInteger statusBig = Numeric.toBigInt(status);
                    ChainWork update = new ChainWork();
                    update.setId(chainWork.getId());
                    update.setTxStatus(ChainWorkTxStatusEnum.OK.getCode());
                    update.setReceiptStatus(statusBig.byteValue());
                    update.setBlockNo(receipt.getBlockNumber().longValue());
                    update.setConfirmAt(System.currentTimeMillis());
                    // 事务确认后，多少秒后开始做业务检查，做啥不关注，由后续业务类关注，
                    // 急的业务可以监听事件，否则就等一个事务确认的时间
                    update.setProcessAt(update.getConfirmAt() + Gkey.EVM_TX_WAIT);
                    // 只有充值任务需要归集检查
                    if (ChainWorkTypeEnum.DEPOSIT.eq(chainWork.getType())) {
                        // 确认的同时标记为归集待检查
                        update.setCollected(ChainWorkCollectedEnum.CHECK.getCode());
                        // 定时任务，要等Listener处理完再检查，防止冲突，所以留出Listener的处理时间
                        update.setCollectAt(update.getConfirmAt() + Gkey.EVM_COLLECT_WAIT);
                    }
                    chainWorkMapper.updateById(update);
                    BeanCopior.copy(update, chainWork);
                    SpringUtil.publishEvent(new ChainWorkConfirmEvent(this, chainWork));
                    log.info("WORK-confirm {}=>ok {}:{} ", chainWork.getType(), chainWork.getId(), chainWork.getHash());
                    return null;
                });
            } catch (Exception e) {
                log.warn(StrUtil.format("WORK-confirm {}=>receipt fail {}:{}", chainWork.getType(), chainWork.getId(), chainWork.getHash()), e);
            }
        }
    }

    /**
     * 集中在一起做业务处理补偿
     */
    //@Scheduled(fixedRate = 6000L, initialDelay = 3000L)
    public void process() {
        List<ChainWork> chainWorks = chainWorkMapper.selectList(new QueryWrapper<ChainWork>().lambda()
                .eq(ChainWork::getChain, ChainEnum.BSC.getCode())
                .eq(ChainWork::getStatus, YesNoByteEnum.YES.getCode())
                .eq(ChainWork::getTxStatus, ChainWorkTxStatusEnum.OK.getCode())
                .eq(ChainWork::getReceiptStatus, YesNoByteEnum.YES.getCode())
                .eq(ChainWork::getProcessed, YesNoByteEnum.NO.getCode())
                .le(ChainWork::getProcessAt, System.currentTimeMillis())
        );

        ChainWorkTypeEnum typeEnum;
        for (ChainWork chainWork : chainWorks) {
            typeEnum = ChainWorkTypeEnum.of(chainWork.getType());
            try {
                switch (typeEnum){
                    case DEPOSIT:
                        // 充值记账
//                        assetService.addDeposit(chainWork);
                        assetService.addDepositByCa(chainWork);
                        break;
                    case WITHDRAW:
                        withdrawFlowService.updateWithdrawStatus(chainWork);
                        break;
                    case GAS:
                        gasWorkService.updateCollectGoon(chainWork);
                        break;
                    case COLLECT:
                        collectWorkService.updateCollceted(chainWork);
                        break;
                    default:
                        break;
                }
//                // 如果是归集的gas转账处理-则要继续归集
//                if (ChainWorkTypeEnum.GAS.eq(chainWork.getType())) {
//                    continueCollect(chainWork);
//                }
//
//                // 归集动作的确认，事务成功与否
//                if (ChainWorkTypeEnum.COLLECT.eq(chainWork.getType())) {
//                    checkCollectOk(chainWork);
//                }
            } catch (Exception e) {
                log.info(StrUtil.format("WORK-process {} fail {}", chainWork.getType(), chainWork.getId()), e);
            }
        }
    }
}
