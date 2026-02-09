package com.cs.copy.evm.server.scan;

import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cs.copy.chain.api.entity.ChainAddress;
import com.cs.copy.chain.api.enums.AddressTypeEnum;
import com.cs.copy.chain.api.service.ChainAddressService;
import com.cs.copy.evm.api.common.EvmConstant;
import com.cs.copy.evm.api.entity.Chain;
import com.cs.copy.evm.api.entity.ChainWork;
import com.cs.copy.evm.api.entity.Symbol;
import com.cs.copy.evm.api.enums.ChainWorkTxStatusEnum;
import com.cs.copy.evm.api.enums.ChainWorkTypeEnum;
import com.cs.copy.evm.api.event.AfterRefreshConfigEvent;
import com.cs.copy.evm.api.service.ChainService;
import com.cs.copy.evm.api.service.ChainWorkService;
import com.cs.copy.evm.api.service.EvmService;
import com.cs.copy.evm.server.config.prop.EvmBscProperties;
import com.cs.copy.evm.server.config.prop.EvmProperties;
import com.cs.copy.evm.server.factory.BscChain;
import com.cs.copy.evm.server.util.EvmUtil;
import com.cs.copy.global.constants.CacheKey;
import com.cs.copy.global.constants.Gkey;
import com.cs.copy.member.api.entity.MemberWallet;
import com.cs.copy.member.api.enums.ChainEnum;
import com.cs.copy.member.api.event.AddWalletEvent;
import com.cs.copy.system.api.dto.GlobalConfigDTO;
import com.cs.copy.system.api.service.ConfigService;
import com.cs.copy.system.server.config.prop.AppProperties;
import com.cs.sp.constant.Constant;
import com.cs.sp.enums.YesNoByteEnum;
import io.reactivex.disposables.Disposable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.Utils;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 多钱包地址扫块
 *
 * @author quzhimin
 * @date 2024/11/24 02:28
 */
@Slf4j
@Component
public class EvmScanHelper {


    public static final String KEY_BLOCK_NUM = "blockNum";
    public static final String KEY_BLOCK_TIME = "blockTime";
    public static final String KEY_START = "start";
    public static final String EVM_TRANSFER = "0xa9059cbb";
    public static final String EVM_TRANSFER_FROM = "0x23b872dd";
    public static final String EVM_TOPUP = "0x184ff47f";
    public static final String EVM_BUY = "0xd6febde8";

    public static final Object LOCK = new Object();

    @Autowired
    private Environment env;

    @Autowired
    private ChainService chainService;
    @Autowired
    private EvmService evmService;

    @Autowired
    private EvmProperties evmProperties;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ChainWorkService chainWorkService;

    @Autowired
    private ConfigService configService;

    private BoundHashOperations<String, String, String> hashOps;
    private Disposable subscribe;
    private Map<String, ChainAddress> depositsMap = new ConcurrentHashMap<>();
    private Map<String, Symbol> depositContractMap = new ConcurrentHashMap<>();
    private GlobalConfigDTO globalConfig;

    private boolean running = false;
    private Boolean inited = null;
    private Long blockNum = null;
    private long latestNum = 0;
    private boolean prod;
    private AtomicLong jumpBlockNo = new AtomicLong(0);

    private Disposable manualDisposable;
    @Autowired
    private ChainAddressService chainAddressService;
    @Autowired
    private AppProperties appProperties;

    @EventListener
    @Order(0)
    public void init(ApplicationStartedEvent e) throws IOException {
//        prod = env.acceptsProfiles(Profiles.of("prod"));
//        hashOps = stringRedisTemplate.boundHashOps(CacheKey.SCAN_EVM);
//        refresh();
    }

    @EventListener
    @Async
    public void afterConfigRefresh(AfterRefreshConfigEvent e){
        log.info("AfterRefreshConfig");
        inited = null;
    }

    public void start() {
        this.running = true;
        hashOps.put(KEY_START, Boolean.TRUE.toString());
    }

    public void stop() {
        this.running = false;
        hashOps.put(KEY_START, Boolean.FALSE.toString());
        if (this.subscribe != null) {
            try {
                this.subscribe.dispose();
            } catch (Exception e) {
            }
            this.subscribe = null;
        }
    }

    public Map<String, String> info() {
        return hashOps.entries();
    }

    /**
     * 加载扫块配置
     *
     * @throws IOException
     */
    public void refresh() throws IOException {
        Chain chain = chainService.getOne(new QueryWrapper<Chain>().lambda()
                .eq(Chain::getStatus, Constant.ONE_BYTE)
                .eq(Chain::getChainType, "evm")
                .orderByDesc(Chain::getWeight)
                .last("limit 1")
        );

        // 暂时只做bsc
        if (EvmConstant.BSC_CHAIN.equals(chain.getChain())) {
            BscChain bscChain = new BscChain(new EvmBscProperties(chain.getChainId(), chain.getRpcUrls()));
            evmService.factory().add(chain.getChain(), bscChain).asDefault(chain.getChain());
        }

        Map<String, String> map = hashOps.entries();
        Long latestNo = null;
        // 1、从redis拿
        // 2、从数据库拿，先blockNo，再startBlockNo
        // 3、直接拿最新的
        if (map.get(KEY_BLOCK_NUM) != null) {
            blockNum = Long.parseLong(map.get(KEY_BLOCK_NUM));
        } else {
            latestNo = evmService.web3j().ethBlockNumber().send().getBlockNumber().longValue();
            blockNum = latestNo;
        }
        if (latestNo == null) {
            latestNo = evmService.web3j().ethBlockNumber().send().getBlockNumber().longValue();
        }

        if (map.get(KEY_START) != null) {
            running = Boolean.parseBoolean(map.get(KEY_START));
        }

        log.info("Evm-scan {}, from {}/{}", running, blockNum, latestNo);
    }

    public void jumpTo(Long blockNum) {
        if (blockNum > 0) {
            jumpBlockNo.set(blockNum);
        }
    }

    public void scan(Long start, Long end) {
        log.info("ManualScan now: {}, from {}=>{}", blockNum, start, end);
        AtomicLong startLong = new AtomicLong(start);
        manualDisposable = evmService.web3j().replayPastBlocksFlowable(
                DefaultBlockParameter.valueOf(BigInteger.valueOf(start)),
                DefaultBlockParameter.valueOf(BigInteger.valueOf(end)),
                true
        ).subscribe(evmBlock -> {
            log.info("ManualScan {}", evmBlock.getBlock().getNumber());
            subscribBlock(evmBlock.getBlock());
            startLong.set(evmBlock.getBlock().getNumber().longValue());
        }, (err -> {
            if (manualDisposable != null) {
                manualDisposable.dispose();
                manualDisposable = null;
            }
            long now = startLong.get();
            if (now < end) {
                scan(now, end);
            }
        }), () -> {
            log.info("ManualScan done");
            if (manualDisposable != null) {
                manualDisposable.dispose();
                manualDisposable = null;
            }
        });
    }

    //@Scheduled(fixedDelay = 3000L, initialDelay = 5000L)
    public void task() {
        if (!running) {
            return;
        }
        if (subscribe != null) {
            return;
        }
        try {
            if (inited == null) {
                inited = false;
                log.info("EVN-scan preload");
                preload();
                log.info("EVN-scan preload end");
                inited = true;
            } else if (Boolean.FALSE.equals(inited)) {
                // 还没加载完，就等加载
                log.info("EVN-scan wait preload");
                return;
            }
            subscribe = evmService.web3j().replayPastAndFutureBlocksFlowable(DefaultBlockParameter.valueOf(BigInteger.valueOf(blockNum)),
                    true).subscribe(evmBlock -> {
                if (jumpBlockNo.get() > 0) {
                    blockNum = jumpBlockNo.get();
                    if (subscribe != null) {
                        subscribe.dispose();
                        subscribe = null;
                    }
                    jumpBlockNo.set(0);
                    return;
                }
                Assert.notNull(evmBlock);
                EthBlock.Block block = evmBlock.getBlock();
                BigInteger blockNo = block.getNumber();
                if (blockNo.longValue() % Constant.ONE_THOUSAND_LONG == 0) {
                    log.info("Evm-scanNo {}", blockNo);
                }
                subscribBlock(block);
                blockNum = blockNo.longValue();
                hashOps.putAll(new HashMap<String, String>() {{
                    put(KEY_BLOCK_NUM, blockNo.toString());
                    put(KEY_BLOCK_TIME, block.getTimestamp().toString());
                }});
                // 如果清了初始化内容，则停止
                if(inited == null){
                    if (subscribe != null) {
                        subscribe.dispose();
                        subscribe = null;
                    }
                }
            }, (error) -> {
                log.warn("Evm-scan subsribe error", error);
                if (subscribe != null) {
                    subscribe.dispose();
                    subscribe = null;
                }

            });
        } catch (Throwable e) {
            log.warn("Evm-scan failed", e);
        }
    }

    private void subscribBlock(EthBlock.Block block) {
        EthBlock.TransactionObject tx;
        for (EthBlock.TransactionResult<EthBlock.TransactionObject> txObj : block.getTransactions()) {
            tx = txObj.get();
            watchDeposits(tx, block);
//            watchGas(tx, block);
        }
    }

    private void watchGas(EthBlock.TransactionObject tx, EthBlock.Block block) {
        if (!depositsMap.containsKey(tx.getTo())) {
            return;
        }
        BigDecimal value = Convert.fromWei(new BigDecimal(tx.getValue()), Convert.Unit.ETHER);
        ChainWork chainWork = new ChainWork();
        chainWork.setChain(ChainEnum.BSC.getCode());
        chainWork.setType(ChainWorkTypeEnum.DEPOSIT.getCode());
        chainWork.setBlockNo(block.getNumber().longValue());
        chainWork.setBlockTime(block.getTimestamp().longValue() * 1000L);
        chainWork.setFromAddr(tx.getFrom());
        chainWork.setToAddr(tx.getTo());
        chainWork.setSymbol(Gkey.BNB);
        chainWork.setAmount(value);
        chainWork.setTxStatus(ChainWorkTxStatusEnum.CONFIRMING.getCode());
        chainWork.setStatus(YesNoByteEnum.YES.getCode());
        chainWork.setHash(tx.getHash());
        chainWork.setGasFee(new BigDecimal(tx.getGas()).divide(BigDecimal.TEN.pow(10)));
        chainWork.setConfirmAt(chainWork.getBlockTime() + Gkey.EVM_CONFIRM);
        try {
            chainWorkService.save(chainWork);
        } catch (DuplicateKeyException e) {
            log.info("SCAN-GAS hash {} exists", tx.getHash());
        }
    }

    // 监听充值
    private void watchDepositByWallet(EthBlock.TransactionObject tx, EthBlock.Block block) {
        //
        if (tx == null || tx.getTo() == null) {
            return;
        }
        Symbol symbol = depositContractMap.get(tx.getTo());
        if (symbol == null) {
            return;
        }
        // 10 + 128
        if (tx.getInput() == null || tx.getInput().length() < 138) {
            return;
        }

        boolean isTransfer = StringUtils.startsWith(tx.getInput(), EVM_TRANSFER);
        boolean isTransferFrom = StringUtils.startsWith(tx.getInput(), EVM_TRANSFER_FROM);
        if (!isTransfer && !isTransferFrom) {
            return;
        }

        String toAddr = null;
        String fromAddr = tx.getFrom();
        BigInteger weiValue = null;
        List<Type> decode = null;
        if (isTransfer) {
            try {
                decode = FunctionReturnDecoder.decode(tx.getInput().substring(10), Utils.convert(Arrays.asList(
                        EvmConstant.TYPE_ADDRESS,
                        EvmConstant.TYPE_UINT256
                )));
                toAddr = ((Address) decode.get(0)).getValue();
                weiValue = ((Uint256) decode.get(1)).getValue();
            } catch (Exception e) {
                log.warn("SCAN-Deposit Hash {} addr error from Transfer", tx.getHash());
                return;
            }
        }

        if (isTransferFrom) {
            try {
                decode = FunctionReturnDecoder.decode(tx.getInput().substring(10), Utils.convert(Arrays.asList(
                        EvmConstant.TYPE_ADDRESS,
                        EvmConstant.TYPE_ADDRESS,
                        EvmConstant.TYPE_UINT256
                )));
                fromAddr = ((Address) decode.get(0)).getValue();
                toAddr = ((Address) decode.get(1)).getValue();
                weiValue = ((Uint256) decode.get(2)).getValue();
            } catch (Exception e) {
                log.warn("SCAN-Deposit Hash {} addr error from TransferFrom", tx.getHash());
                return;
            }
        }

        // 收款地址不在监听钱包里
        if (!depositsMap.containsKey(toAddr)) {
            return;
        }

        // 转
        BigDecimal value = EvmUtil.fromWei(weiValue, symbol.getBaseDecimals());
        ChainWork chainWork = new ChainWork();
        chainWork.setChain(ChainEnum.BSC.getCode());
        chainWork.setType(ChainWorkTypeEnum.DEPOSIT.getCode());
        chainWork.setBlockNo(block.getNumber().longValue());
        chainWork.setCreateAt(System.currentTimeMillis());
        chainWork.setQueueAt(chainWork.getCreateAt());
        chainWork.setBlockTime(block.getTimestamp().longValue() * 1000L);
        chainWork.setFromAddr(fromAddr);
        chainWork.setToAddr(toAddr);
        chainWork.setContract(symbol.getBaseCa());
        chainWork.setSymbol(symbol.getSymbol());
        chainWork.setAmount(value);
        chainWork.setTxStatus(ChainWorkTxStatusEnum.CONFIRMING.getCode());
        chainWork.setStatus(YesNoByteEnum.YES.getCode());
        chainWork.setHash(tx.getHash());
        chainWork.setGasFee(Convert.fromWei(new BigDecimal(tx.getGas()), Convert.Unit.ETHER));
        chainWork.setGasPrice(new BigDecimal(tx.getGasPrice()));
        chainWork.setConfirmAt(chainWork.getBlockTime() + Gkey.EVM_CONFIRM);
        try {
            chainWorkService.save(chainWork);
            log.info("SCAN-DEPOSIT {}, {}, {}", toAddr, value.stripTrailingZeros().toPlainString(), tx.getHash());
        } catch (DuplicateKeyException e) {
            log.info("SCAN-DEPOSIT Exists {}, {}, {}", toAddr, value.stripTrailingZeros().toPlainString(), tx.getHash());
        }
    }

    // 监听充值
    private void watchDeposits(EthBlock.TransactionObject tx, EthBlock.Block block) {
        //
        if(tx == null || tx.getTo() == null) {
            return;
        }
        if(!depositsMap.containsKey(tx.getTo())){
            return;
        }

        // 10 + 128
        if (tx.getInput() == null || tx.getInput().length() < 138) {
            return;
        }

        boolean isTopup = StringUtils.startsWith(tx.getInput(), EVM_TOPUP);
        if (!isTopup) {
            return;
        }

        BigInteger uid = null;
        String fromAddr = tx.getFrom();
        BigInteger weiValue = null;
        List<Type> decode = null;
        if (isTopup) {
            try {
                decode = FunctionReturnDecoder.decode(tx.getInput().substring(10), Utils.convert(Arrays.asList(
                        EvmConstant.TYPE_UINT256,
                        EvmConstant.TYPE_UINT256
                )));
                uid = ((Uint256) decode.get(0)).getValue();
                weiValue = ((Uint256) decode.get(1)).getValue();
            } catch (Exception e) {
                log.warn("SCAN-Deposit Hash {} addr error from Transfer", tx.getHash());
                return;
            }
        }

        // 转
        BigDecimal value = EvmUtil.fromWei(weiValue, Gkey.USDT_DECIMALS);
        ChainWork chainWork = new ChainWork();
        chainWork.setChain(ChainEnum.BSC.getCode());
        chainWork.setType(ChainWorkTypeEnum.DEPOSIT.getCode());
        chainWork.setBlockNo(block.getNumber().longValue());
        chainWork.setCreateAt(System.currentTimeMillis());
        chainWork.setQueueAt(chainWork.getCreateAt());
        chainWork.setBlockTime(block.getTimestamp().longValue() * 1000L);
        chainWork.setFromAddr(fromAddr);
        chainWork.setToAddr(tx.getTo());
        chainWork.setContract(tx.getTo());
        chainWork.setSymbol(Gkey.TOKEN);
        chainWork.setAmount(value);
        JSONObject json = new JSONObject();
        json.put("uid", uid);
        chainWork.setParam(json.toJSONString());
        chainWork.setTxStatus(ChainWorkTxStatusEnum.CONFIRMING.getCode());
        chainWork.setStatus(YesNoByteEnum.YES.getCode());
        chainWork.setHash(tx.getHash());
        chainWork.setGasFee(new BigDecimal(tx.getGas()).divide(BigDecimal.TEN.pow(10)));
        chainWork.setGasPrice(new BigDecimal(tx.getGasPrice()));
        chainWork.setConfirmAt(chainWork.getBlockTime() + Gkey.EVM_CONFIRM);
        try {
            chainWorkService.save(chainWork);
            log.info("SCAN-DEPOSIT {}, {}, {}", uid, value.stripTrailingZeros().toPlainString(), tx.getHash());
        } catch (DuplicateKeyException e) {
            log.info("SCAN-DEPOSIT Exists {}, {}, {}", uid, value.stripTrailingZeros().toPlainString(), tx.getHash());
        }
    }

    /**
     * 通知未处理的
     */
    private void preload() {
        globalConfig = configService.getGlobalConfig(GlobalConfigDTO.class);
        List<ChainAddress> chainAddresses = chainAddressService.listRechargeAndCached(AddressTypeEnum.RECHARGE.getCode(), ChainEnum.BSC.getCode());
        depositsMap.clear();
        chainAddresses.stream().forEach(addr -> depositsMap.put(addr.getAddr(), addr));
        log.info("DEPOSIT-ADDR {}, {}", chainAddresses.size(), depositsMap.keySet());
    }


    /**
     * 有人注册，并且更新账户成功，则追加到扫块监听
     *
     * @param e
     */
    @TransactionalEventListener
    @Async
    public void addWallet(AddWalletEvent e) {
        MemberWallet memberWallet = e.getMemberWallet();
        // TODO: 换了钱包，新用户注册影响
        ChainAddress one = chainAddressService.getOne(new QueryWrapper<ChainAddress>().lambda()
                .eq(ChainAddress::getChain, ChainEnum.BSC.getCode())
                .eq(ChainAddress::getAddr, memberWallet.getWallet())
                .eq(ChainAddress::getType, AddressTypeEnum.RECHARGE.getCode())
        );
        if (one != null) {
            log.info("Reg-Wallet new {},{}", memberWallet.getUid(), one.getAddr());
            depositsMap.put(one.getAddr(), one);
        } else {
            log.warn("REG-Wallet-watch not exists {}:{}", memberWallet.getUid(), memberWallet.getWallet());
        }
    }
}

