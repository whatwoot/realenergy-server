package com.cs.energy.tron.task;

import cn.hutool.core.util.HexUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cs.energy.system.api.service.ConfigService;
import com.cs.energy.tron.api.entity.TronEnergyRentalPrice;
import com.cs.energy.tron.api.service.TronEnergyRentalPriceService;
import com.cs.energy.tron.entity.EnergyAccount;
import com.cs.energy.tron.entity.EnergyRental;
import com.cs.energy.tron.service.EnergyAccountService;
import com.cs.energy.tron.service.EnergyRentalService;
import com.cs.energy.tron.service.TronV2Client;
import com.cs.energy.tron.service.impl.EnergyAccountServiceImpl;
import com.cs.energy.tron.service.impl.EnergyRentalServiceImpl;
import com.cs.energy.tron.util.AesUtil;
import com.cs.gasstation.dto.BalanceResponse;
import com.cs.gasstation.dto.CreateOrderResponse;
import com.cs.gasstation.dto.QueryRecordResponse;
import com.cs.gasstation.service.GasStationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tron能量租赁监控任务
 * 功能：
 * 1. 每秒监控转账记录，处理租用能量申请
 * 2. 每分钟检查到期租赁，收回能量
 * 3. 每分钟检查能量恢复，更新能量池
 */
@Slf4j
@Component
public class TronV2EnergyMonitor {

    private final EnergyAccountServiceImpl accountService;
    private final EnergyRentalServiceImpl rentalService;
    private final TronV2Client tronClient;
    private final TronEnergyRentalPriceService rentalPriceService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private GasStationService gasStationService;


    /**
     * TRX数量与TronEnergyRentalPrice的映射
     * key: TRX数量(SUN单位)
     * value: TronEnergyRentalPrice对象
     */
    private final Map<Long, TronEnergyRentalPrice> trxToPriceMap = new ConcurrentHashMap<>();

    /**
     * 从小到大排序的TRX数量数组
     */
    private volatile Long[] sortedTrxAmounts = new Long[0];

    /**
     * 价格映射是否已初始化
     */
    private volatile boolean priceMapInitialized = false;

    /**
     * 能量接收地址（承租方转账的目标地址）
     */
//    @Value("${tron.rental.deposit-address:}")
//    private String depositAddress;

//    /**
//     * 能量比率：1 TRX = energyRatio 能量
//     */
//    @Value("${tron.rental.energy-ratio:10}")
//    private long energyRatio;

    /**
     * 默认租赁时长（毫秒），默认10分钟
     */
    @Value("${tron.rental.duration:600000}")
    private long defaultDuration;

    /**
     * 最小租赁金额（SUN），默认1 TRX = 1000000 SUN
     */
    @Value("${tron.rental.min-amount:1000000}")
    private long minAmount;

    @Value("${tron.aes.key}")
    private String aesKey;

    /**
     * 上次扫描时间戳
     */
    private volatile long lastScanTime = 0;//System.currentTimeMillis() - 60000;

    /**
     * TRX与能量的汇率（每1 TRX能获得的能量点数）
     * 在monitorRecovery任务中定期从链上更新
     */
    private volatile float energyPricePerTrx = 0;

    public TronV2EnergyMonitor(
            @Qualifier("tronV2EnergyAccountService") EnergyAccountService accountService,
            @Qualifier("tronV2EnergyRentalService") EnergyRentalService rentalService,
            @Qualifier("tronV2Client") TronV2Client tronClient,
            TronEnergyRentalPriceService rentalPriceService) {
        this.accountService = (EnergyAccountServiceImpl) accountService;
        this.rentalService = (EnergyRentalServiceImpl) rentalService;
        this.tronClient = tronClient;
        this.rentalPriceService = rentalPriceService;

//        // 确保价格映射已初始化
//        if (!priceMapInitialized) {
//            initializePriceMap();
//        }

        // 初始化能量汇率
        try {
            if(energyPricePerTrx == 0) {
                float newEnergyPrice = tronClient.getEnergyPricePerTrx();
                if (newEnergyPrice > 0) {
                    energyPricePerTrx = newEnergyPrice;
                    log.info("能量汇率初始化: 1 TRX = {} 能量", energyPricePerTrx);
                }
            }
        } catch (Exception e) {
            log.error("能量汇率初始化失败", e);
            throw e;
        }

    }

    // ==================== 任务1: 每秒监控转账记录 ====================

    /**
     * 每3秒查询指定账号的转账记录
     * 如果收到承租方的转账，处理租用能量申请
     */
    @Scheduled(fixedRate = 3000)
    public void monitorDeposits() {
        String depositAddress = configService.getValueByKey("depositAddress");
        if (depositAddress == null || depositAddress.isEmpty()) {
            return;
        }

        long now = System.currentTimeMillis();
        if(lastScanTime==0){
            lastScanTime = this.rentalService.getLastRentTime()>0?this.rentalService.getLastRentTime():now - 3*60*60000;
        }
        try {
            // 查询最近的转入交易
            JSONArray txs = tronClient.getTransactions(depositAddress , lastScanTime);//1769512381326L);//lastScanTime);
            lastScanTime = now;

            if (txs == null || txs.isEmpty()) {
                return;
            }

            log.debug("查询到 {} 笔新交易", txs.size());

            for (int i = 0; i < txs.size(); i++) {
                processTransaction(txs.getJSONObject(i));
            }
        } catch (Exception e) {
            log.error("监控转账记录异常", e);
        }
    }

    /**
     * 处理单笔交易
     */
    private void processTransaction(JSONObject tx) {
        try {

            String txHash = tx.getStr("txID");

            // 检查是否已处理
            if (rentalService.isRequestTxProcessed(txHash)) {
                return;
            }

            // 解析交易内容
            JSONObject rawData = tx.getJSONObject("raw_data");
            if (rawData == null || !rawData.containsKey("contract")) {
                return;
            }

            JSONObject contract = rawData.getJSONArray("contract").getJSONObject(0);
            String type = contract.getStr("type");

            // 只处理TRX转账
            if (!"TransferContract".equals(type)) {
                return;
            }

            JSONObject parameter = contract.getJSONObject("parameter");
            JSONObject value = parameter.getJSONObject("value");

            String toAddress = value.getStr("to_address");
            String depositHex = tronClient.toHex(configService.getValueByKey("depositAddress"));

            // 验证接收地址
            if (toAddress == null || !toAddress.equalsIgnoreCase(depositHex)) {
                return;
            }

            String fromAddress = value.getStr("owner_address");
            long amount = value.getLong("amount", 0L);
            if(amount<minAmount){
                log.warn("转账金额不足，忽略: {} from {}", amount, tronClient.toBase58(fromAddress));
                return;
            }

            // 从交易备注信息中提取 rentAddress
            String rentAddress = null;
            if (rawData.containsKey("data")) {
                try {
                    String dataHex = rawData.getStr("data");
                    if (dataHex != null && !dataHex.isEmpty()) {
                        // 将十六进制转换为字符串
                        byte[] dataBytes = HexUtil.decodeHex(dataHex);
                        String memo = new String(dataBytes, StandardCharsets.UTF_8);

                        // 尝试解析为Tron地址（Base58格式，以T开头）
                        if (memo.trim().startsWith("T") && memo.trim().length() >= 34) {
                            rentAddress = memo.trim();
                            log.info("从备注中提取承租地址: {}", rentAddress);
                        }
                    }
                } catch (Exception e) {
                    log.warn("解析交易备注失败", e);
                }
            }

            // 如果备注中没有地址，则使用转账发起方地址
            if (rentAddress == null) {
                rentAddress = fromAddress;
                log.info("备注中没有地址，使用转账发起方地址: {}", tronClient.toBase58(rentAddress));
            }

            // 创建租赁记录
            EnergyRental rental = new EnergyRental();
            rental.setFromAddress(tronClient.toBase58(fromAddress));
            rental.setRentAddress(rentAddress.startsWith("T") ? rentAddress : tronClient.toBase58(rentAddress));
            rental.setPrice(new BigDecimal(0));
            rental.setDuration(defaultDuration);
            rental.setCreateAt(System.currentTimeMillis());
            rental.setRequestTxHash(txHash);
            rental.setRequestTrx(amount);
            rental.setRetries(0);
            rental.setStatus(0); // 待处理



            rentalService.save(rental);

            // 执行能量代理
            if(rental.getStatus()==0) {
                executeRental(rental);
            }

        } catch (Exception e) {
            log.error("处理交易异常", e);
        }
    }

    /**
     * 执行能量租赁
     * 从能量池获取最大可用能量的账号，代理能量给承租方
     */
    private void executeRental(EnergyRental rental) {
//        // 根据转账金额匹配价格档位
//        TronEnergyRentalPrice priceInfo = findBestPriceForAmount(rental.getRequestTrx());
//        if (priceInfo == null) {
//            log.warn("未找到匹配的价格档位，转账金额: {} from {}", rental.getRequestTrx(), rental.getFromAddress());
//            rental.setStatus(5);
//            rentalService.updateById(rental);
//            return;
//        }

//        log.info("匹配价格档位: 转账金额={} SUN, 价格档位TRX={} SUN, 能量={}",
//                rental.getRequestTrx(), priceInfo.getTrxAmount(), priceInfo.getEnergyAmount());


        long rentalEnergyUnitNums = rental.getRequestTrx()/configService.getLongByKey("rentalUnitTrx");
        if(rentalEnergyUnitNums<1){
            log.warn("转账金额不足以租用最小能量单位，忽略: {} from {}", rental.getRequestTrx(), rental.getFromAddress());
            rental.setStatus(5);
            rentalService.updateById(rental);
            return;
        }
        long rentalEnergyAmount = configService.getLongByKey("energyUnitAmount") * rentalEnergyUnitNums;
        long rentalEnergyTrx = (long)Math.ceil(rentalEnergyAmount * 1e6 / energyPricePerTrx);
        log.info("收到租用申请: {} 金额: {} SUN, 匹配档位{}, 能量: {}",
                rental.getFromAddress(), rental.getRequestTrx() , rentalEnergyUnitNums, rentalEnergyAmount);

        rental.setEnergyAmount(rentalEnergyAmount); // 使用价格档位的能量数量
        rental.setEnergyTrxAmount(rentalEnergyTrx);

        if(!rental.getFromAddress().equals(rental.getRentAddress())) {
            if(!tronClient.isAddressActivated(rental.getRentAddress())){
                log.warn("承租方地址未激活，租赁失败: {}", rental.getRentAddress());
                rental.setStatus(6);
                rentalService.updateById(rental);
                return;
            }
        }

        // 从优先队列获取满足条件的能量账号
        EnergyAccount provider = accountService.pollMaxAvailableAccount(rental.getEnergyAmount());

        if (provider == null) {
            log.warn("没有足够能量的提供者，租赁待处理: ID={}", rental.getId());
            return;
        }

        log.info("执行能量代理: provider: {} -> {}, 能量: {}",
                provider.getId(), rental.getRentAddress(), rental.getEnergyAmount());

        if(provider.getLessorType().compareTo(0)==0){
            selfRental(rental, provider);
        }else{
            thirdRental(rental, provider);
        }
    }

    private void selfRental(EnergyRental rental, EnergyAccount provider) {
        try {
            // 调用Tron Stake 2.0 API代理能量
            String delegateTxHash = tronClient.delegateResource(
                    provider.getAddress(),
                    rental.getRentAddress(),
                    rental.getEnergyTrxAmount(),
                    AesUtil.decrypt(provider.getPrivateKey(),this.aesKey),
                    provider.getPermissionId()
            );

            if (delegateTxHash != null) {
                // 更新租赁记录
                long now = System.currentTimeMillis();
                rental.setProviderAddress(provider.getAddress());
                rental.setDelegateTxHash(delegateTxHash);
                rental.setStatus(2); // 租赁成功
                rental.setLessorType(provider.getLessorType());
                rental.setExpireAt(now + rental.getDuration());
                rental.setRetries(rental.getRetries() + 1);
                rentalService.updateById(rental);

                // 加入到期队列
                rentalService.addToExpirationQueue(rental);

                // 更新提供者可用能量
                provider.setAvailableEnergy(provider.getAvailableEnergy() - rental.getEnergyAmount());
                provider.setRentEnergy(provider.getRentEnergy() + rental.getEnergyAmount());
                accountService.updateAvailableEnergy(provider);

                log.info("能量代理成功: 租赁ID={}, 交易Hash={}", rental.getId(), delegateTxHash);
            } else {
                rental.setRetries(rental.getRetries() + 1);
                rentalService.updateById(rental);
                // 代理失败，将账号放回能量池
                accountService.addToPool(provider);
                log.error("能量代理失败: 租赁ID={}", rental.getId());
            }
        }catch (Exception e){
            rental.setRetries(rental.getRetries() + 1);
            rentalService.updateById(rental);
            // 代理失败，将账号放回能量池
            accountService.addToPool(provider);
            log.error("能量代理失败: 租赁ID={}", rental.getId(),e);
        }
    }

    private void thirdRental(EnergyRental rental, EnergyAccount provider) {
        try {
            CreateOrderResponse response = gasStationService.createOrderByAmount(
                    rental.getId().toString(),
                    rental.getRentAddress(),
                    "20001", // serviceChargeType，10分钟:10010, 1小时:20001, 1天:30001
                    rental.getEnergyAmount().intValue(),
                    0
            );

            long now = System.currentTimeMillis();
            rental.setProviderAddress(rental.getRentAddress());
            rental.setDelegateTxHash(response.getTradeNo());
            rental.setTraderNo(response.getTradeNo());
            rental.setStatus(1); // 发送订单成功，等待Gas Station处理
            rental.setRetries(rental.getRetries()+1);
            rental.setLessorType(provider.getLessorType());
            rental.setExpireAt(now + rental.getDuration());//只有type0需要回收
            rentalService.updateById(rental);

        }catch (Exception e){
            rental.setRetries(rental.getRetries()+1);
            rentalService.updateById(rental);
            // 代理失败，将账号放回能量池
            accountService.addToPool(provider);
            log.error("能量代理失败: 租赁ID={},error:{}", rental.getId(),e.getMessage());
        }
    }

    // ==================== 任务2: 每分钟检查到期租赁 ====================

    /**
     * 每分钟检查到期租赁，收回能量
     */
    @Scheduled(fixedRate = 60000)
    public void monitorExpiration() {
        long now = System.currentTimeMillis();

        while (true) {
            // 查看最早到期的租赁
            EnergyRental rental = rentalService.peekEarliestExpiration();
            if (rental == null || rental.getExpireAt() > now) {
                break;
            }

            // 弹出到期的租赁
            rental = rentalService.pollEarliestExpiration();
            if (rental == null) {
                break;
            }

            // 从数据库获取最新状态
            EnergyRental dbRental = rentalService.getById(rental.getId());
            if (dbRental == null || dbRental.getStatus() != 2) {
                continue;
            }

            processExpiredRental(dbRental);
        }
    }

    @Scheduled(fixedRate = 5*60000) //每5分钟从数据库查看一次，以防漏掉需要回收的记录
    public void monitorExpirationV2() {
        long now = System.currentTimeMillis();

        try {
            // 查询处于待回收的租赁记录，只能是status==2的记录，lessor==0
            List<EnergyRental> takebackRentals = rentalService.list(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<EnergyRental>()
                            .eq(EnergyRental::getLessorType,0)//其它类型的由第三方自行收回
                            .gt(EnergyRental::getStatus, 1)//0,未开始，1已下单等待Gas Station处理，2已租赁成功，3已过期，4，已收回，5没有合适的价格档位，6地址未
                            .lt(EnergyRental::getStatus,4)//2和3都需要回收，2是正常到期，3是过期未回收的
                            .lt(EnergyRental::getExpireAt, now)
                            .orderByAsc(EnergyRental::getCreateAt)
            );

            if (takebackRentals.isEmpty()) {
                return;
            }

            log.info("发现 {} 条到期的租赁记录", takebackRentals.size());

            for (EnergyRental rental : takebackRentals) {

                processExpiredRental(rental);
            }
        } catch (Exception e) {
            log.error("能量回收失败：", e);
        }
    }

    /**
     * 处理到期的租赁记录
     */
    private void processExpiredRental(EnergyRental rental) {
        EnergyAccount provider = accountService.getOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<EnergyAccount>()
                        .eq(EnergyAccount::getAddress, rental.getProviderAddress())
        );

        if (provider == null) {
            log.error("找不到能量提供者: {}", rental.getProviderAddress());
            return;
        }

        log.info("收回能量: {} <- {}, 能量: {}",
                provider.getAddress(), rental.getRentAddress(), rental.getEnergyAmount());

        // 调用Tron Stake 2.0 API取消代理
        try {
            String reclaimTxHash = tronClient.unDelegateResource(
                    provider.getAddress(),
                    rental.getRentAddress(),
                    rental.getEnergyTrxAmount(),
                    AesUtil.decrypt(provider.getPrivateKey(), this.aesKey)
            );

            if (reclaimTxHash != null) {
                // 更新租赁状态
                rental.setStatus(4); // 已收回
                rental.setReclaimTxHash(reclaimTxHash);
                rentalService.updateById(rental);

                provider.setRentEnergy(provider.getRentEnergy() - rental.getEnergyAmount());
                provider.setAvailableEnergy(provider.getAvailableEnergy() + rental.getEnergyAmount());
                accountService.updateById(provider);

                log.info("能量收回成功: 租赁ID={}, 交易Hash={}", rental.getId(), reclaimTxHash);
                // 提供者可用能量将在monitorRecovery中更新
            } else {
                if (rental.getStatus() != 3) {
                    rental.setStatus(3); // 回收失败，进入过期状态，等待重试
                    rentalService.updateById(rental);
                }

                // 收回失败，重新加入到期队列稍后重试
                rental.setExpireAt(System.currentTimeMillis() + 60000); // 1分钟后重试
                rentalService.addToExpirationQueue(rental);
                log.error("能量收回失败，稍后重试: 租赁ID={}", rental.getId());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ==================== 任务3: 每分钟重试未完成的租赁 ====================

    /**
     * 每分钟检查未完成的租赁记录并重试
     * 查询status==0且创建时间超过1分钟的记录，重新执行租赁
     */
    @Scheduled(fixedRate = 60000)
    public void monitorPendingRentals() {long now = System.currentTimeMillis();
        long oneMinuteAgo = now - 60000;

        try {
            // 查询待处理且创建时间超过1分钟的租赁记录
            List<EnergyRental> pendingRentals = rentalService.list(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<EnergyRental>()
                            .lt(EnergyRental::getStatus, 2)//0,未开始，1已下单等待Gas Station处理，2已租赁成功，3已过期，4，已收回，5没有合适的价格档位，6地址未
                            .lt(EnergyRental::getRetries, 3)
                            .lt(EnergyRental::getCreateAt, oneMinuteAgo)
                            .orderByAsc(EnergyRental::getCreateAt)
            );

            if (pendingRentals.isEmpty()) {
                return;
            }

            log.info("发现 {} 条待重试的租赁记录", pendingRentals.size());

            for (EnergyRental rental : pendingRentals) {
                try {
                    log.info("重试租赁: ID={}, 用户={}, 创建时间={}",
                            rental.getId(), rental.getRentAddress(), rental.getCreateAt());
                    if(rental.getStatus() == 0) {
                        executeRental(rental);
                    }else if(rental.getLessorType() > 0) {
                        // 如果之前是已下单等待Gas Station处理，检查订单状态
                        try {
                            List<QueryRecordResponse> orders = gasStationService.queryOrderStatus(rental.getId().toString());
                            for (QueryRecordResponse order : orders) {
                                if (order.getTradeNo().equals(rental.getTraderNo()) && order.getRequestId().equals(rental.getId().toString())) {
                                    if (order.getStatus() == 1) {
                                        rental.setExpenseTrx(order.getAmount().multiply(new BigDecimal(1000000L)).longValue());
                                        rental.setStatus(4);//代理资源成功，此处直接改为已回收，因为不需要我们自己回收
                                        rentalService.updateById(rental);
                                    } else if (order.getStatus() == 2) {
                                        rental.setStatus(7);//订单失败
                                        rentalService.updateById(rental);
                                    } else if (order.getStatus() == 10) {
                                        rental.setExpenseTrx(order.getAmount().multiply(new BigDecimal(1000000L)).longValue());
                                        rental.setStatus(4);//已回收
                                        rentalService.updateById(rental);
                                    }
                                }
                            }
                        }catch (Exception e) {
                            log.error("查询Gas Station订单状态异常: ID={}", rental.getId(), e);
                        }
                    }
                } catch (Exception e) {
                    log.error("重试租赁失败: ID={}", rental.getId(), e);
                }
            }
        } catch (Exception e) {
            log.error("监控待处理租赁异常", e);
        }
    }

    // ==================== 任务4: 每分钟检查能量恢复 ====================

    /**
     * 每分钟对能量池中可用能量<总能量的账号，进行能量恢复检查
     */
    @Scheduled(fixedRate = 60000)
    public void monitorRecovery() {
//        // 更新能量汇率
//        try {
//            float newEnergyPrice = tronClient.getEnergyPricePerTrx();
//            if (newEnergyPrice > 0) {
//                energyPricePerTrx = newEnergyPrice;
//                log.info("更新能量汇率: 1 TRX = {} 能量", energyPricePerTrx);
//            }
//        } catch (Exception e) {
//            log.error("更新能量汇率失败", e);
//        }

//        List<EnergyAccount> accounts = accountService.getAllPoolAccounts();
        List<EnergyAccount> accounts = accountService.list(new LambdaQueryWrapper<EnergyAccount>()
                .eq(EnergyAccount::getStatus, 1)
        );

        for (EnergyAccount account : accounts) {
            if (account.getLessorType() == 0) {
                // 只检查可用能量<总能量的账号
                if (account.getAvailableEnergy() + account.getRentEnergy() >= account.getTotalEnergy()) {
                    continue;
                }

                try {
                    JSONObject resource = tronClient.getAccountResource(account.getAddress());
                    if (resource != null && resource.size() > 0) {
                        // 获取能量限制和已使用能量
                        long energyLimit = resource.getLong("EnergyLimit", 0L);
                        long energyUsed = resource.getLong("EnergyUsed", 0L);

                        // 计算当前可用能量
                        long newAvailable = energyLimit - energyUsed;

                        // 如果能量有变化，更新
                        if (newAvailable != account.getAvailableEnergy() || energyLimit != account.getTotalEnergy()) {
                            log.info("账号能量更新: {} 总能量: {} -> {}, 可用: {} -> {}",
                                    account.getAddress(),
                                    account.getTotalEnergy(), energyLimit,
                                    account.getAvailableEnergy(), newAvailable);

                            account.setTotalEnergy(energyLimit);
                            account.setAvailableEnergy(newAvailable);
                            accountService.updateAvailableEnergy(account);
                        }
                    }
                } catch (Exception e) {
                    log.error("检查账号能量恢复异常: {}", account.getAddress(), e);
                }
            } else if (account.getLessorType()==1) {
                try{
                    BalanceResponse balanceResponse = gasStationService.queryBalance();
                    if(balanceResponse!=null){
                        account.setTotalEnergy(balanceResponse.getBalance().multiply(BigDecimal.valueOf(1e6)).longValue());
                        account.setAvailableEnergy(balanceResponse.getBalance().multiply(BigDecimal.valueOf(1e6)).longValue());
                        accountService.updateAvailableEnergy(account);
                    }
                }catch (Exception e){
                    log.error("查询Gas Station余额异常: {}", account.getAddress(), e);
                }
            }
        }
    }

    // ==================== 工具方法 ====================

    /**
     * 从数据库加载价格映射到内存
     * 在第一次租赁时调用
     */
    private void initializePriceMap() {
        if (priceMapInitialized) {
            return;
        }

        synchronized (this) {
            if (priceMapInitialized) {
                return;
            }

            try {
                List<TronEnergyRentalPrice> priceList = rentalPriceService.list();
                if (priceList != null && !priceList.isEmpty()) {
                    // 构建映射
                    for (TronEnergyRentalPrice price : priceList) {
                        trxToPriceMap.put(price.getTrxAmount(), price);
                    }

                    // 构建排序数组
                    List<Long> trxAmountList = new ArrayList<>(trxToPriceMap.keySet());
                    Collections.sort(trxAmountList);
                    sortedTrxAmounts = trxAmountList.toArray(new Long[0]);

                    log.info("初始化价格映射完成，共加载 {} 条价格数据", priceList.size());
                    log.info("价格档位 (SUN): {}", Arrays.toString(sortedTrxAmounts));
                } else {
                    log.warn("数据库中没有价格数据");
                }
                priceMapInitialized = true;
            } catch (Exception e) {
                log.error("初始化价格映射失败", e);
            }
        }
    }

    /**
     * 根据用户支付的TRX数量，查找不大于该数量的最大价格档位
     * 使用二分查找算法
     *
     * @param userAmount 用户支付的TRX数量（SUN单位）
     * @return 匹配的价格信息，如果没有找到则返回null
     */
    private TronEnergyRentalPrice findBestPriceForAmount(long userAmount) {
        if (sortedTrxAmounts.length == 0) {
            return null;
        }

        // 如果用户金额小于最小档位，返回null
        if (userAmount < sortedTrxAmounts[0]) {
            return null;
        }

        // 如果用户金额大于等于最大档位，返回最大档位
        if (userAmount >= sortedTrxAmounts[sortedTrxAmounts.length - 1]) {
            return trxToPriceMap.get(sortedTrxAmounts[sortedTrxAmounts.length - 1]);
        }

        // 二分查找：找到不大于userAmount的最大值
        int left = 0;
        int right = sortedTrxAmounts.length - 1;
        int result = 0;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (sortedTrxAmounts[mid] <= userAmount) {
                result = mid;
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return trxToPriceMap.get(sortedTrxAmounts[result]);
    }

    /**
     * 根据TRX数量获取对应的能量数量
     * @param trxAmount TRX数量（SUN单位）
     * @return 对应的能量数量，如果没有找到则返回null
     */
    public Long getEnergyAmountByTrx(long trxAmount) {
        if (!priceMapInitialized) {
            initializePriceMap();
        }
        TronEnergyRentalPrice price = trxToPriceMap.get(trxAmount);
        return price != null ? price.getEnergyAmount() : null;
    }

    /**
     * 获取当前的TRX与能量汇率
     * @return 每1 TRX能获得的能量点数，如果未初始化返回0
     */
    public float getEnergyPricePerTrx() {
        return energyPricePerTrx;
    }

}
