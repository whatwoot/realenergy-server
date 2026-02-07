package com.cs.copy.evm.server.queue;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cs.copy.asset.api.entity.WithdrawFlow;
import com.cs.copy.asset.api.enums.WithdrawStatusEnum;
import com.cs.copy.asset.api.service.WithdrawFlowService;
import com.cs.copy.asset.server.mapper.WithdrawFlowMapper;
import com.cs.copy.chain.api.entity.ChainAddress;
import com.cs.copy.chain.api.enums.AddressTypeEnum;
import com.cs.copy.chain.api.service.ChainAddressService;
import com.cs.copy.chain.server.mapper.ChainAddressMapper;
import com.cs.copy.evm.api.dto.ChainWorkQueueDTO;
import com.cs.copy.evm.api.entity.ChainWork;
import com.cs.copy.evm.api.enums.ChainWorkTxStatusEnum;
import com.cs.copy.evm.api.enums.ChainWorkTypeEnum;
import com.cs.copy.evm.api.service.EvmService;
import com.cs.copy.evm.server.mapper.ChainWorkMapper;
import com.cs.copy.global.constants.Gkey;
import com.cs.copy.member.api.enums.ChainEnum;
import com.cs.copy.system.api.dto.GlobalConfigDTO;
import com.cs.copy.system.api.service.ConfigService;
import com.cs.sp.enums.YesNoByteEnum;
import com.cs.web.spring.helper.aeshelper.AesHelper;
import com.cs.web.spring.helper.tgbot.dto.TgNotifyDTO;
import com.cs.web.spring.helper.tgbot.event.TgNotifyEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.core.Response;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.utils.Convert;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;

/**
 * 处理evm相关的事务
 * 如流水转账
 *
 * @author fiona
 * @date 2024/12/21 02:23
 */
@Slf4j
@Component
public class WithdrawWorkQueueService {

    @Autowired
    private ChainWorkMapper chainWorkMapper;

    protected final LinkedBlockingDeque<ChainWorkQueueDTO> queue = new LinkedBlockingDeque<>();
    protected ExecutorService executor;

    @Autowired
    private EvmService evmService;

    @Autowired
    private AesHelper aesHelper;

    @Autowired
    private ChainAddressService chainAddressService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private WithdrawFlowService withdrawFlowService;
    @Autowired
    private WithdrawFlowMapper withdrawFlowMapper;
    @Autowired
    private ChainAddressMapper chainAddressMapper;

    public WithdrawWorkQueueService() {
        doInit();
    }

    public void doInit() {
        executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), new BasicThreadFactory.Builder()
                .namingPattern("withdraw-dq-%d").daemon(true).build(), new ThreadPoolExecutor.AbortPolicy());
        executor.submit(this::runTask);
    }

    public void add(ChainWork add) {
        ChainWorkQueueDTO queueDTO = new ChainWorkQueueDTO(add.getId(), add);
        if (!getQueue().contains(queueDTO)) {
            getQueue().add(queueDTO);
        }
    }

    public LinkedBlockingDeque<ChainWorkQueueDTO> getQueue() {
        return queue;
    }

    private void runTask() {
        ChainWorkQueueDTO take = null;
        while (!Thread.currentThread().isInterrupted()) {
            try {
                take = getQueue().take();
                if (take != null) {
                    // 处理完成后，加入到延时确认队列
                    work(take);
                }
            } catch (Exception e) {
                if (take != null) {
                    log.warn(StrUtil.format("WORK-queue failed {}=>{}, {}", take.getChainWork().getType(), take.getChainWork().getFromAddr(),
                            take.getChainWork().getToAddr(), take.getChainWork().getAmount()), e);
                    TgNotifyDTO tgNotifyDTO = new TgNotifyDTO();
                    tgNotifyDTO.setScene("用户提现失败");
                    tgNotifyDTO.setOriented("dev");
                    tgNotifyDTO.setMember(StrUtil.format("用户: {}", take.getChainWork().getParam()));
                    tgNotifyDTO.setThings(StrUtil.format("提现：{} => {}",
                            take.getChainWork().getAmount(), take.getChainWork().getToAddr()
                    ));
                    tgNotifyDTO.setTx(StrUtil.format("转账任务ID: {}", take.getId()));
                    tgNotifyDTO.setCreateAt(take.getChainWork().getCreateAt());
                    SpringUtil.publishEvent(new TgNotifyEvent(this, tgNotifyDTO));
                } else {
                    log.warn("WORK-queue failed", e);
                }
            }
        }
    }

    /**
     * 执行任务
     *
     * @param queueDTO
     */
    private void work(ChainWorkQueueDTO queueDTO) {
        GlobalConfigDTO config = configService.getGlobalConfig(GlobalConfigDTO.class);
        doWithdraw(queueDTO.getChainWork(), config);
    }

    /**
     * @param take
     * @param config
     */
    private void doWithdraw(ChainWork take, GlobalConfigDTO config) {
        ChainWork chainWork = chainWorkMapper.selectById(take.getId());
        if (!ChainWorkTxStatusEnum.WAIT.eq(chainWork.getTxStatus())) {
            return;
        }
        List<ChainAddress> chainAddresses = chainAddressService.list(new QueryWrapper<ChainAddress>().lambda()
                .eq(ChainAddress::getChain, ChainEnum.BSC.getCode())
                .and(qr -> qr.eq(ChainAddress::getSymbol, take.getSymbol()).or()
                        .eq(ChainAddress::getSymbol, Gkey.SYMBOL_ALL))
                .eq(ChainAddress::getType, AddressTypeEnum.WITHDRAW.getCode())
                .eq(ChainAddress::getStatus, YesNoByteEnum.YES.getCode())
                .orderByDesc(ChainAddress::getWeight)
        );
        if (chainAddresses.isEmpty()) {
            log.warn("Withdraw no address {}", take.getId());
            //delayQueueTime(take, config);
            return;
        }
        // 找到最近的
        ChainAddress fromAddr = choose(chainAddresses, take);
        // 按时间睡眠
        Long sleepTime = fromAddr.getUpdateAt() + 15 * 1000 - System.currentTimeMillis();
        if (sleepTime > 0) {
            ThreadUtil.safeSleep(sleepTime);
        }

        Credentials credentials = Credentials.create(aesHelper.decrypt(fromAddr.getPrivKey()));

        // 先改状态，防止意外重启后，重新触发转账问题
        transactionTemplate.execute(tx -> {
            ChainWork update = new ChainWork();
            update.setId(take.getId());
            update.setTxStatus(ChainWorkTxStatusEnum.DOING.getCode());
            update.setFromAddr(credentials.getAddress());
            chainWorkMapper.updateById(update);
            log.info("Withdraw doing {}, {} {}, {}=>{}", take.getId(), take.getAmount().stripTrailingZeros().toPlainString(),
                    take.getSymbol(), update.getFromAddr(), take.getToAddr());
            Long relateId = take.getRelateId();
            if (relateId == null) {
                return null;
            }
            boolean isMerchant = isMerchant(take);
            WithdrawFlow withdrawFlow = new WithdrawFlow();
            withdrawFlow.setId(relateId);
            withdrawFlow.setStatus(WithdrawStatusEnum.CONFIRMING.getCode());
            withdrawFlowMapper.update(withdrawFlow, Wrappers.lambdaUpdate(WithdrawFlow.class)
                    .eq(WithdrawFlow::getId, relateId)
                    .eq(WithdrawFlow::getStatus, WithdrawStatusEnum.AUDITED.getCode())
            );
            return null;
        });

        ChainWork update = new ChainWork();
        update.setId(take.getId());
        try {
            Pair<Response.Error, EthSendTransaction> pair = evmService.broadcast(credentials, take.getContract(), "transfer", Arrays.asList(
                    new Address(take.getToAddr()),
                    new Uint256(Convert.toWei(take.getAmount(), Convert.Unit.ETHER).toBigInteger())
            ), Collections.emptyList());
            // 失败
            if (pair.getLeft() != null) {
                update.setTxStatus(ChainWorkTxStatusEnum.FAIL.getCode());
                update.setErrMsg(StrUtil.format("{}:{}", pair.getLeft().getCode(), pair.getLeft().getMessage()));
            } else {
                update.setHash(pair.getRight().getTransactionHash());
                // 确认时的账单里没有包含区块时间，所以这里暂时使用发事务的时间做区块时间
                update.setBlockTime(System.currentTimeMillis());
                update.setConfirmAt(update.getBlockTime() + Gkey.EVM_CONFIRM);
                update.setTxStatus(ChainWorkTxStatusEnum.CONFIRMING.getCode());
            }
        } catch (Throwable e) {
            update.setTxStatus(ChainWorkTxStatusEnum.FAIL.getCode());
            update.setErrMsg(StringUtils.truncate(e.getMessage(), 400));
        }
        chainWorkMapper.updateById(update);
        log.info("WORK-tx {}, {}, {}", update.getId(), update.getHash(), update.getErrMsg());
        if (ChainWorkTxStatusEnum.CONFIRMING.eq(update.getTxStatus())) {
            // TODO: 自管理nonce可以做到并行
//            Utils.sleep(15L);
            ChainAddress updateWait = new ChainAddress();
            updateWait.setId(fromAddr.getId());
            updateWait.setUpdateAt(System.currentTimeMillis());
            chainAddressMapper.updateById(updateWait);
        }
    }

    private ChainAddress choose(List<ChainAddress> chainAddresses, ChainWork take) {
        if (chainAddresses.size() == 1) {
            return chainAddresses.get(0);
        }
        chainAddresses.sort(Comparator.comparing(ChainAddress::getUpdateAt));
        return chainAddresses.get(0);
    }

    private boolean isMerchant(ChainWork take) {
        boolean isMerchant = false;
        String param = take.getParam();
        try {
            JSONObject paramJson = JSONObject.parseObject(param);
            isMerchant = paramJson.containsKey(Gkey.MERCHANT);
        } catch (Throwable t) {
        }
        return Gkey.MERCHANT.equals(param) || isMerchant;
    }

    /**
     * 把数据库中的任务加到队列
     */
    //@Scheduled(fixedDelay = 5000L, initialDelay = 5000L)
    public void addToQueue() {
        List<ChainWork> chainWorks = chainWorkMapper.selectList(new QueryWrapper<ChainWork>().lambda()
                .eq(ChainWork::getChain, ChainEnum.BSC.getCode())
                .eq(ChainWork::getType, ChainWorkTypeEnum.WITHDRAW.getCode())
                .eq(ChainWork::getStatus, YesNoByteEnum.YES.getCode())
                .eq(ChainWork::getTxStatus, ChainWorkTxStatusEnum.WAIT.getMsg())
                .lt(ChainWork::getQueueAt, System.currentTimeMillis())
                .orderByAsc(ChainWork::getQueueAt)
        );
        for (ChainWork chainWork : chainWorks) {
            add(chainWork);
        }
    }


    public void delayQueueTime(ChainWork chainWork, GlobalConfigDTO config) {
        ChainWork update = new ChainWork();
        update.setId(chainWork.getId());
        update.setQueueAt(System.currentTimeMillis() + Gkey.HALF_MINUTE_MILLISECOND);
        chainWorkMapper.updateById(update);
    }
}
