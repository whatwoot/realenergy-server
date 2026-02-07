package com.cs.copy.evm.server.queue;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cs.copy.chain.server.mapper.ChainAddressMapper;
import com.cs.copy.evm.api.entity.ChainWork;
import com.cs.copy.evm.api.entity.Symbol;
import com.cs.copy.evm.api.enums.ChainWorkCollectedEnum;
import com.cs.copy.evm.api.enums.ChainWorkProcessedEnum;
import com.cs.copy.evm.api.enums.ChainWorkTxStatusEnum;
import com.cs.copy.evm.api.enums.ChainWorkTypeEnum;
import com.cs.copy.evm.api.service.EvmService;
import com.cs.copy.evm.server.mapper.ChainWorkMapper;
import com.cs.copy.evm.server.mapper.SymbolMapper;
import com.cs.copy.global.constants.Gkey;
import com.cs.copy.member.api.enums.ChainEnum;
import com.cs.copy.chain.api.entity.ChainAddress;
import com.cs.copy.chain.api.enums.AddressTypeEnum;
import com.cs.sp.enums.YesNoByteEnum;
import com.cs.web.spring.helper.aeshelper.AesHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;
import org.web3j.protocol.core.Response;
import org.web3j.protocol.core.methods.response.EthSendTransaction;

import java.util.List;

/**
 * @author fiona
 * @date 2024/12/26 05:31
 */
@Slf4j
@Component
public class GasWorkService {


    @Autowired
    private ChainWorkMapper chainWorkMapper;

    @Autowired
    private ChainAddressMapper chainAddressMapper;

    @Autowired
    private SymbolMapper symbolMapper;

    @Autowired
    private EvmService evmService;

    @Autowired
    private AesHelper aesHelper;

    @Autowired
    private TransactionTemplate transactionTemplate;

    /**
     * 找到待处理的转gas事件
     */
    //@Scheduled(fixedRate = 6000L, initialDelay = 5000L)
    public void findGas() {
        List<ChainWork> gasWork = chainWorkMapper.selectList(new QueryWrapper<ChainWork>().lambda()
                .eq(ChainWork::getChain, ChainEnum.BSC.getCode())
                .eq(ChainWork::getType, ChainWorkTypeEnum.GAS.getCode())
                .eq(ChainWork::getStatus, YesNoByteEnum.YES.getCode())
                .eq(ChainWork::getTxStatus, ChainWorkTxStatusEnum.WAIT.getCode())
                .le(ChainWork::getQueueAt, System.currentTimeMillis())
        );
        for (ChainWork chainWork : gasWork) {
            try {
                doTransferGas(chainWork);
            } catch (Exception e) {
                log.warn(StrUtil.format("GAS-transfer fail {}", chainWork.getId()), e);
            }
        }
    }

    /**
     * 转gas
     * @param chainWork
     */
    private void doTransferGas(ChainWork chainWork) {
        // 找到手续费来源地址
        ChainAddress feeAddr = chainAddressMapper.selectOne(new QueryWrapper<ChainAddress>().lambda()
                .eq(ChainAddress::getChain, chainWork.getChain())
                .eq(ChainAddress::getSymbol, chainWork.getSymbol())
                .eq(ChainAddress::getType, AddressTypeEnum.FEE.getCode())
                .orderByDesc(ChainAddress::getWeight)
                .last("limit 1")
        );
        if(feeAddr == null){
            markFail(chainWork, "no fee addr");
            return;
        }
        Symbol symbol = symbolMapper.selectOne(new QueryWrapper<Symbol>().lambda()
                .eq(Symbol::getChain, chainWork.getChain())
                .eq(Symbol::getSymbol, chainWork.getSymbol())
        );
        if(symbol == null){
            markFail(chainWork, StrUtil.format("no symbol {}", chainWork.getSymbol()));
            return;
        }

        ChainWork updated = new ChainWork();
        updated.setId(chainWork.getId());
        updated.setFromAddr(feeAddr.getAddr());
        Pair<Response.Error, EthSendTransaction> transfer;
        try {
            log.info("GAS-transfer {}, {}", chainWork.getToAddr(), chainWork.getAmount().stripTrailingZeros().toPlainString());
            transfer = evmService.transfer(aesHelper.decrypt(feeAddr.getPrivKey()),
                    chainWork.getToAddr(), chainWork.getAmount());
            if (transfer.getLeft() != null) {
                updated.setTxStatus(ChainWorkTxStatusEnum.FAIL.getCode());
                updated.setErrMsg(StrUtil.format("{}:{}", transfer.getLeft().getCode(), transfer.getLeft().getMessage()));
            } else {
                updated.setHash(transfer.getRight().getTransactionHash());
                updated.setBlockTime(System.currentTimeMillis());
                // gas费的确认时间可以缩短
                updated.setConfirmAt(updated.getBlockTime() + Gkey.EVM_TX_WAIT);
                updated.setTxStatus(ChainWorkTxStatusEnum.CONFIRMING.getCode());
            }
        }catch (Exception ex){
            log.warn(StrUtil.format("GAS-transfer failed {}", chainWork.getId()), ex);
            updated.setTxStatus(ChainWorkTxStatusEnum.FAIL.getCode());
            updated.setErrMsg(StringUtils.truncate(ex.getMessage(), 400));
        }
        chainWorkMapper.update(updated, Wrappers.lambdaUpdate(ChainWork.class)
                .eq(ChainWork::getId, chainWork.getId())
                .eq(ChainWork::getTxStatus, ChainWorkTxStatusEnum.WAIT.getCode())
        );
        if (ChainWorkTxStatusEnum.CONFIRMING.eq(updated.getTxStatus())) {
            ThreadUtil.safeSleep(15000);
        }
    }

    private void markFail(ChainWork chainWork, String msg){
        markGasStatus(chainWork, ChainWorkProcessedEnum.FAIL.getCode(), msg, null);
    }

    private void markGasStatus(ChainWork chainWork, Byte code, String msg, Long queueAt) {
        ChainWork update = new ChainWork();
        update.setId(chainWork.getId());
        if (code != null) {
            update.setProcessed(code);
        }
        update.setProcessMsg(msg);
        if (queueAt != null) {
            update.setQueueAt(queueAt);
        }
        chainWorkMapper.updateById(update);
    }

    /**
     * 继续归集
     * @param chainWork
     */
    public void updateCollectGoon(ChainWork chainWork) {
        ChainWork proccessed  = new ChainWork();
        proccessed.setId(chainWork.getId());
        proccessed.setProcessed(ChainWorkProcessedEnum.OK.getCode());
        proccessed.setProcessAt(System.currentTimeMillis());
        int row = chainWorkMapper.update(proccessed, Wrappers.lambdaUpdate(ChainWork.class)
                .eq(ChainWork::getId, chainWork.getId())
        );
        if(row > 0 && chainWork.getRelateId() != null){
            //获取到关联的归集
            Long relateId = chainWork.getRelateId();
            ChainWork collect = chainWorkMapper.selectById(relateId);
            if(collect == null){
                return;
            }
            ChainWork updated = new ChainWork();
            updated.setId(relateId);
            updated.setCollected(ChainWorkCollectedEnum.WAIT_COLLECT.getCode());
            int updateRow = chainWorkMapper.update(updated, Wrappers.lambdaUpdate(ChainWork.class)
                    .eq(ChainWork::getId, relateId)
                    .eq(ChainWork::getCollected, ChainWorkCollectedEnum.WAIT_GAS.getCode())
            );
        }
    }
}
