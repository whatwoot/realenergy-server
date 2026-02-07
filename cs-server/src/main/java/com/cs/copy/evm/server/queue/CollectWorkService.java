package com.cs.copy.evm.server.queue;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cs.copy.chain.server.mapper.ChainAddressMapper;
import com.cs.copy.evm.api.common.EvmConstant;
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
import com.cs.copy.evm.server.util.EvmUtil;
import com.cs.web.spring.helper.aeshelper.AesHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.core.Response;
import org.web3j.protocol.core.methods.response.EthSendTransaction;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.cs.sp.common.WebAssert.expectGt0;

/**
 * @author fiona
 * @date 2024/12/26 05:31
 */
@Slf4j
@Component
public class CollectWorkService {


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
     * 找到符合条件的充值记录，做是否归集的检查
     * 1、已确认的充值记录
     * 2、并且记账为成功的
     * 3、并且标记为要归集的(转账确认时会标记）
     */
//    @Scheduled(fixedRate = 6000L, initialDelay = 5000L)
    public void process() {
        List<ChainWork> checkCollectWork = chainWorkMapper.selectList(new QueryWrapper<ChainWork>().lambda()
                .eq(ChainWork::getChain, ChainEnum.BSC.getCode())
                .eq(ChainWork::getType, ChainWorkTypeEnum.DEPOSIT.getCode())
                .eq(ChainWork::getStatus, YesNoByteEnum.YES.getCode())
                .eq(ChainWork::getTxStatus, ChainWorkTxStatusEnum.OK.getCode())
                .eq(ChainWork::getReceiptStatus, YesNoByteEnum.YES.getCode())
                .eq(ChainWork::getCollected, ChainWorkCollectedEnum.CHECK.getCode())
                .le(ChainWork::getCollectAt, System.currentTimeMillis())
        );
        // 检查是否归集
        for (ChainWork chainWork : checkCollectWork) {
            try {
                checkCollect(chainWork);
            } catch (Exception e) {
                log.warn("COLLECT-check {} fail", chainWork.getHash(), e);
            }
        }
    }

    /**
     * 检查是否需要归集
     *
     * @param chainWork
     */
    public void checkCollect(ChainWork chainWork) {
        // 找到充值地址
        ChainAddress chainAddress = chainAddressMapper.selectOne(new QueryWrapper<ChainAddress>().lambda()
                .eq(ChainAddress::getChain, chainWork.getChain())
                .eq(ChainAddress::getAddr, chainWork.getToAddr())
                .eq(ChainAddress::getType, AddressTypeEnum.RECHARGE.getCode())
        );
        // 收款记录未对应到地址记录
        if (chainAddress == null) {
            log.info("COLLECT-check addr missing {}", chainWork.getToAddr());
            markCollectStatus(chainWork, ChainWorkCollectedEnum.FAIL.getCode(), "addr missing");
            return;
        }

        Symbol symbol = symbolMapper.selectOne(new QueryWrapper<Symbol>().lambda()
                .eq(Symbol::getChain, chainWork.getChain())
                .eq(Symbol::getSymbol, chainWork.getSymbol())
        );

        if (symbol == null) {
            log.info("COLLECT-check symbol missing {}:{}=> {}", chainAddress.getChain(), chainAddress.getSymbol(), chainAddress.getAddr());
            markCollectStatus(chainWork, ChainWorkCollectedEnum.FAIL.getCode(), "symbol missing");
            return;
        }

        // 不用归集的，则可以直接成功
        if (!YesNoByteEnum.YES.eq(symbol.getCollected())) {
            markCollectStatus(chainWork, ChainWorkCollectedEnum.OK.getCode(), "not collect");
            return;
        }

        Pair<Response.Error, List<Type>> pair = null;
        // 查余额
        try {
            pair = evmService.ethCall(chainWork.getFromAddr(), chainWork.getContract(),
                    EvmConstant.BALANCE_OF, Arrays.asList(new Address(chainAddress.getAddr())
                    ), Arrays.asList(EvmConstant.TYPE_UINT256));
            if (pair.getLeft() != null) {
                log.info("COLLECT-balanceOf {} fail", chainWork.getToAddr());
                // 这里要重试
                markCollectStatus(chainWork, ChainWorkCollectedEnum.CHECK.getCode(), "balanceOf get failed");
                return;
            }
        } catch (Exception e) {
            log.warn(StrUtil.format("COLLECT-balanceOf {} fail", chainWork.getToAddr()), e);
            markCollectStatus(chainWork, ChainWorkCollectedEnum.CHECK.getCode(), StringUtils.truncate(e.getMessage(), 400));
            return;
        }

        // 检查是否需要归集
        List<Type> res = pair.getRight();
        BigInteger amount = ((Uint256) res.get(0)).getValue();
        BigDecimal balance = EvmUtil.fromWei(amount, symbol.getBaseDecimals());
        // 低于最低归集要求
        if (balance.compareTo(symbol.getCollectMinAmount()) < 0) {
            markCollectStatus(chainWork, ChainWorkCollectedEnum.OK.getCode(), StrUtil.format("Balance {} above min",
                    balance.stripTrailingZeros().toPlainString()));
            return;
        }

        // 插入一个待归集的事务
        transactionTemplate.execute(tx -> {
            //插入待归集的事务和修改原来的状态，同时处理
            ChainWork collectWork = new ChainWork();
            collectWork.setType(ChainWorkTypeEnum.COLLECT.getCode());
            collectWork.setChain(ChainEnum.BSC.getCode());
            collectWork.setSymbol(chainAddress.getSymbol());
            // 从收款地址->归集地址
            collectWork.setFromAddr(chainAddress.getAddr());
            // 归集事务无收款地址，处理归集时再拉取，金额也实时获取
            collectWork.setContract(chainWork.getContract());
            collectWork.setCreateAt(System.currentTimeMillis());
            collectWork.setQueueAt(collectWork.getCreateAt());
            // 关联充值记录
            collectWork.setRelateId(chainWork.getId());
            collectWork.setTxStatus(ChainWorkTxStatusEnum.WAIT.getCode());
            // 标记为待归集
            collectWork.setCollected(ChainWorkCollectedEnum.WAIT_COLLECT.getCode());
            collectWork.setStatus(YesNoByteEnum.YES.getCode());
            chainWorkMapper.insert(collectWork);
            // 标记原来的提现为待归集
            ChainWork update = new ChainWork();
            update.setCollected(ChainWorkCollectedEnum.WAIT_COLLECT.getCode());
            int row = chainWorkMapper.update(update, Wrappers.lambdaUpdate(ChainWork.class)
                    .eq(ChainWork::getId, chainWork.getId())
                    .eq(ChainWork::getCollected, ChainWorkCollectedEnum.CHECK.getCode())
            );
            expectGt0(row, "chk.collect.already");
            return null;
        });
    }


    /**
     * 找到待处理的归集事件去执行归集
     * 来源有2种，
     * 1、充值后，检查了需要归集，插入的
     * 2、其他渠道直接插入的
     */
    //@Scheduled(fixedRate = 6000L, initialDelay = 5000L)
    public void findCollect() {
        List<ChainWork> collectWork = chainWorkMapper.selectList(new QueryWrapper<ChainWork>().lambda()
                .eq(ChainWork::getChain, ChainEnum.BSC.getCode())
                .eq(ChainWork::getType, ChainWorkTypeEnum.COLLECT.getCode())
                .eq(ChainWork::getStatus, YesNoByteEnum.YES.getCode())
                .eq(ChainWork::getTxStatus, ChainWorkTxStatusEnum.WAIT.getCode())
                .eq(ChainWork::getCollected, ChainWorkCollectedEnum.WAIT_COLLECT.getCode())
                .le(ChainWork::getQueueAt, System.currentTimeMillis())
        );
        for (ChainWork chainWork : collectWork) {
            try {
                doCollect(chainWork);
            } catch (Exception e) {
                log.warn(StrUtil.format(""), e);
            }
        }
    }

    /**
     * 执行归集
     *
     * @param chainWork
     */
    private void doCollect(ChainWork chainWork) throws IOException {

        // 找到充值地址
        ChainAddress chainAddress = chainAddressMapper.selectOne(new QueryWrapper<ChainAddress>().lambda()
                .eq(ChainAddress::getChain, chainWork.getChain())
                .eq(ChainAddress::getAddr, chainWork.getFromAddr())
                .eq(ChainAddress::getSymbol, chainWork.getSymbol())
                .eq(ChainAddress::getType, AddressTypeEnum.RECHARGE.getCode())
        );
        // 找到对应的地址
        if (chainAddress == null) {
            log.info("COLLECT-check addr missing {}", chainWork.getFromAddr());
            markCollectStatus(chainWork, ChainWorkCollectedEnum.FAIL.getCode(), "addr missing");
            return;
        }

        Symbol symbol = symbolMapper.selectOne(new QueryWrapper<Symbol>().lambda()
                .eq(Symbol::getChain, chainWork.getChain())
                .eq(Symbol::getSymbol, chainWork.getSymbol())
        );

        if (symbol == null) {
            log.info("COLLECT-symbol missing {}:{}=> {}", chainWork.getChain(), chainWork.getSymbol(), chainWork.getFromAddr());
            markCollectStatus(chainWork, ChainWorkCollectedEnum.FAIL.getCode(), "symbol missing");
            return;
        }

        // 不用归集的，则可以直接成功
        if (!YesNoByteEnum.YES.eq(symbol.getCollected())) {
            markCollectStatus(chainWork, ChainWorkCollectedEnum.OK.getCode(), "not collect");
            return;
        }

        // 还是要查询余额
        BigInteger amount = getWeiBalance(chainWork);
        if (amount == null) {
            return;
        }

        BigDecimal balance = EvmUtil.fromWei(amount, symbol.getBaseDecimals());
        // 低于最低归集要求
        if (balance.compareTo(symbol.getCollectMinAmount()) < 0) {
            markCollectStatus(chainWork, ChainWorkCollectedEnum.OK.getCode(), StrUtil.format("Balance {} above min",
                    balance.stripTrailingZeros().toPlainString()));
            return;
        }

        // 获取归集地址
        ChainAddress collectAddr = chainAddressMapper.selectOne(new QueryWrapper<ChainAddress>().lambda()
                .eq(ChainAddress::getChain, chainWork.getChain())
                .eq(ChainAddress::getSymbol, chainWork.getSymbol())
                .eq(ChainAddress::getType, AddressTypeEnum.COLLECT.getCode())
                .orderByDesc(ChainAddress::getWeight)
                .last("limit 1")
        );
        // 没找到归集地址
        if (collectAddr == null) {
            // 30秒后，再检查
            markWait30(chainWork, "no collect addr");
            return;
        }

        Pair<Response.Error, EthSendTransaction> transfer;
        try {
            // 归集
            transfer = evmService.broadcast(aesHelper.decrypt(chainAddress.getPrivKey()),
                    chainWork.getContract(), EvmConstant.TRANSFER, Arrays.asList(
                            new Address(collectAddr.getAddr()),
                            new Uint256(amount)
                    ), Collections.emptyList());
            if (transfer.getLeft() != null) {
                Integer code = transfer.getLeft().getCode();
                String message = transfer.getLeft().getMessage();
                // -32000: insufficient funds 表示bsc不足
                // 3： exceeds balance 表示代币不足
                // TOKEN余额不足
                if (EvmConstant.TOKEN_NOT_ENOUGH_CODE.equals(code) && message != null && message.contains(EvmConstant.TOKEN_NOT_ENOUGH_MSG)) {
                    // 30秒重新检查（这种场景是有其他事务也提了）
                    markWait15(chainWork, "Token not enough");
                    return;
                }

                if (EvmConstant.MAIN_COIN_NOT_ENOUGH_CODE.equals(code) && message != null && message.contains(EvmConstant.MAIN_COIN_NOT_ENOUGH_MSG)) {
                    // 存在，则插入一个待转gas的事务
                    transactionTemplate.execute(tx -> {
                        // 插入转gas的，同时修改归集的
                        ChainWork gasWork = new ChainWork();
                        gasWork.setChain(ChainEnum.BSC.getCode());
                        gasWork.setType(ChainWorkTypeEnum.GAS.getCode());
                        gasWork.setToAddr(chainWork.getFromAddr());
                        // gas费代币
                        gasWork.setSymbol(symbol.getGasCoin());
                        // gas费单次转账数量
                        gasWork.setAmount(symbol.getCollectGas());
                        gasWork.setCreateAt(System.currentTimeMillis());
                        gasWork.setQueueAt(gasWork.getCreateAt());
                        // 关联充值
                        gasWork.setRelateId(chainWork.getId());
                        gasWork.setTxStatus(ChainWorkTxStatusEnum.WAIT.getCode());
                        gasWork.setStatus(YesNoByteEnum.YES.getCode());
                        chainWorkMapper.insert(gasWork);
                        // 改成等gas
                        ChainWork work = new ChainWork();
                        work.setCollected(ChainWorkCollectedEnum.WAIT_GAS.getCode());
                        chainWorkMapper.update(work, Wrappers.lambdaUpdate(ChainWork.class)
                                .eq(ChainWork::getId, chainWork.getId())
                                .eq(ChainWork::getCollected, ChainWorkCollectedEnum.WAIT_COLLECT.getCode())
                        );
                        return null;
                    });
                    return;
                }
                // 其他异常，则记录
                // 取最长400位存储
                markCollectStatus(chainWork, ChainWorkCollectedEnum.FAIL.getCode(), message);
                return;
            }
        } catch (Exception e) {
            // 30秒重新检查（这种场景是有其他事务也提了）
            markCollectStatus(chainWork, ChainWorkCollectedEnum.FAIL.getCode(), e.getMessage());
            return;
        }
        ChainWork updateCollect = new ChainWork();
        updateCollect.setId(chainWork.getId());
        // 记录归集地址
        updateCollect.setToAddr(collectAddr.getAddr());
        // 记录实际金额
        updateCollect.setAmount(balance);
        // 延迟确认
        updateCollect.setBlockTime(System.currentTimeMillis());
        updateCollect.setConfirmAt(updateCollect.getBlockTime() + Gkey.EVM_TX_WAIT);
        updateCollect.setHash(transfer.getRight().getTransactionHash());
        // 关联充值
        updateCollect.setTxStatus(ChainWorkTxStatusEnum.CONFIRMING.getCode());
        chainWorkMapper.updateById(updateCollect);
    }

    @Nullable
    private BigInteger getWeiBalance(ChainWork chainWork) {
        Pair<Response.Error, List<Type>> pair = null;
        // 查余额
        try {
            pair = evmService.ethCall(chainWork.getFromAddr(), chainWork.getContract(),
                    EvmConstant.BALANCE_OF, Arrays.asList(new Address(chainWork.getFromAddr())
                    ), Arrays.asList(EvmConstant.TYPE_UINT256));
            if (pair.getLeft() != null) {
                log.info("COLLECT-balanceOf {} fail", chainWork.getToAddr());
                // 15秒后重试
                markWait15(chainWork, "balanceOf get failed");
                return null;
            }
            return ((Uint256) pair.getRight().get(0)).getValue();
        } catch (Exception e) {
            log.warn(StrUtil.format("COLLECT-balanceOf {} fail", chainWork.getToAddr()), e);
            // 15秒后重试
            markWait15(chainWork, e.getMessage());
        }
        return null;
    }


    /**
     * 标记充值记录的归集状态
     */
    private void markCollectStatus(ChainWork chainWork, Byte code, String msg) {
        markCollectStatus(chainWork, code, StringUtils.truncate(msg), null);
    }

    private void markWait30(ChainWork chainWork, String msg) {
        markCollectStatus(chainWork, null, StringUtils.truncate(msg), System.currentTimeMillis() + Gkey.COLLECT_AGAIN_WAIT);
    }

    private void markWait15(ChainWork chainWork, String msg) {
        markCollectStatus(chainWork, null, StringUtils.truncate(msg), System.currentTimeMillis() + Gkey.EVM_TX_MIN_WAIT);
    }

    private void markCollectStatus(ChainWork chainWork, Byte code, String msg, Long queueAt) {
        ChainWork update = new ChainWork();
        update.setId(chainWork.getId());
        if (code != null) {
            update.setCollected(code);
        }
        update.setCollectMsg(msg);
        if (queueAt != null) {
            update.setQueueAt(queueAt);
        }
        chainWorkMapper.updateById(update);
    }

    /**
     * 标记已处理
     * @param chainWork
     */
    public void updateCollceted(ChainWork chainWork) {
        ChainWork collected = new ChainWork();
        collected.setId(chainWork.getId());
        collected.setProcessed(ChainWorkProcessedEnum.OK.getCode());
        collected.setProcessAt(System.currentTimeMillis());
        collected.setCollected(ChainWorkCollectedEnum.OK.getCode());
        int row = chainWorkMapper.update(collected, Wrappers.lambdaUpdate(ChainWork.class)
                .eq(ChainWork::getId, chainWork.getId())
                .eq(ChainWork::getCollected, ChainWorkCollectedEnum.WAIT_COLLECT.getCode())
        );
        log.info("Collect-tx {} updated, {}=>ok: {}", chainWork.getType(), chainWork.getId(), row);
        if(row > 0 && chainWork.getRelateId() != null){
            // 找到充值的钱
            Long relateId = chainWork.getRelateId();
            ChainWork deposit = chainWorkMapper.selectById(relateId);
            if(deposit == null){
                return;
            }
            ChainWork depositOk = new ChainWork();
            depositOk.setId(relateId);
            depositOk.setCollected(ChainWorkCollectedEnum.OK.getCode());
            depositOk.setCollectAt(System.currentTimeMillis());
            chainWorkMapper.update(depositOk, Wrappers.lambdaUpdate(ChainWork.class)
                    .eq(ChainWork::getId, relateId)
                    .eq(ChainWork::getCollected, ChainWorkCollectedEnum.WAIT_COLLECT.getCode())
            );
        }
    }
}
