package com.cs.energy.tron.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cs.energy.system.api.event.ReBuildCacheEvent;
import com.cs.energy.system.api.event.ReFreshEnergyPoolEvent;
import com.cs.energy.system.api.service.ConfigService;
import com.cs.energy.tron.api.vo.EnergyStatsVO;
import com.cs.energy.tron.entity.EnergyAccount;
import com.cs.energy.tron.mapper.EnergyAccountMapper;
import com.cs.energy.tron.service.EnergyAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 能量池账号服务实现
 * 使用优先队列管理能量池，按可用能量降序排列
 */
@Slf4j
@Service("tronV2EnergyAccountService")
public class EnergyAccountServiceImpl extends ServiceImpl<EnergyAccountMapper, EnergyAccount> implements EnergyAccountService {

    @Autowired
    private ConfigService configService;
    /**
     * 能量池优先队列 - 按可用能量降序排列
     */
    private final PriorityBlockingQueue<EnergyAccount> energyPool = new PriorityBlockingQueue<>(
            100,
            Comparator.comparingLong(EnergyAccount::getAvailableEnergy).reversed()
    );

    private final PriorityBlockingQueue<EnergyAccount> thirdEnergyPool = new PriorityBlockingQueue<>(
            100,
            Comparator.comparingLong(EnergyAccount::getAvailableEnergy).reversed()
    );

    /**
     * 账号ID到账号对象的映射，用于快速查找和更新
     */
    private final ConcurrentHashMap<Long, EnergyAccount> accountMap = new ConcurrentHashMap<>();

    /**
     * 读写锁，保护优先队列操作
     */
    private final ReentrantReadWriteLock queueLock = new ReentrantReadWriteLock();

    @PostConstruct
    public void init() {
        initPriorityQueue();
    }

    /**
     * 如果后台管理修改或添加了账号，需要调用此方法重新初始化优先队列
     */
    @Override
    public void initPriorityQueue() {
        queueLock.writeLock().lock();
        try {
            energyPool.clear();
            accountMap.clear();
            thirdEnergyPool.clear();

            // 从数据库加载所有活跃的能量账号
            List<EnergyAccount> accounts = list(new LambdaQueryWrapper<EnergyAccount>()
                    .eq(EnergyAccount::getStatus, 1)
            );

            for (EnergyAccount account : accounts) {
                if(account.getLessorType().compareTo(0) == 0){
                    energyPool.offer(account);
                }else {
                    thirdEnergyPool.offer(account);
                }
                accountMap.put(account.getId(), account);
            }

            log.info("能量池优先队列初始化完成，共加载 {} 个账号", accounts.size());
        } finally {
            queueLock.writeLock().unlock();
        }
    }

    /**
     * 首先从本地优先队列中获取可用能量最大的账号，如果满足最小能量需求则返回；否则尝试从第三方能量池获取满足条件的账号
     * @param minEnergy 最小能量需求
     * @return
     */
    @Override
    public EnergyAccount pollMaxAvailableAccount(long minEnergy) {
        queueLock.writeLock().lock();
        try {
            // 查看队首元素
            EnergyAccount account = null;
            EnergyAccount top = energyPool.peek();
            log.info("energyPool peek top: {} 可用能量: {}", top != null ? top.getAddress() : "null", top != null ? top.getAvailableEnergy() : "null");
            if (top == null || top.getAvailableEnergy() < minEnergy) {
                top = thirdEnergyPool.peek();
                log.info("thirdEnergyPool peek top: {} 可用能量: {}", top != null ? top.getAddress() : "null", top != null ? top.getAvailableEnergy() : "null");
                if(top == null ) {
                    return null;
                }else{
                    account = thirdEnergyPool.poll();
                }

            }else{
                account = energyPool.poll();
            }

            // 弹出并返回
            if (account != null) {
                // 从映射中移除，待更新后再加回
                accountMap.remove(account.getId());
            }
            return account;
        } finally {
            queueLock.writeLock().unlock();
        }
    }

    @Override
    public void updateAvailableEnergy(EnergyAccount account) {
        queueLock.writeLock().lock();
        try {
            PriorityBlockingQueue<EnergyAccount> curPool = account.getLessorType().compareTo(0) == 0 ? energyPool : thirdEnergyPool ;
            // 如果账号在映射中，先从队列移除
            EnergyAccount existing = accountMap.get(account.getId());
            if (existing != null) {
                curPool.remove(existing);
            }

            // 更新数据库
            account.setUpdateAt(System.currentTimeMillis());
            updateById(account);

            // 如果账号状态为活跃，重新加入队列
            if (account.getStatus() != null && account.getStatus() == 1) {
                curPool.offer(account);
                accountMap.put(account.getId(), account);
            } else {
                accountMap.remove(account.getId());
            }

            log.debug("更新账号能量: {} 可用能量: {}", account.getAddress(), account.getAvailableEnergy());
        } finally {
            queueLock.writeLock().unlock();
        }
    }

    @Override
    public void addToPool(EnergyAccount account) {
        queueLock.writeLock().lock();
        try {
            PriorityBlockingQueue<EnergyAccount> curPool = account.getLessorType().compareTo(0) == 0 ? energyPool : thirdEnergyPool ;
            if (account.getStatus() != null && account.getStatus() == 1) {
                // 确保不重复添加
                EnergyAccount existing = accountMap.get(account.getId());
                if (existing != null) {
                    curPool.remove(existing);
                }
                curPool.offer(account);
                accountMap.put(account.getId(), account);
                log.info("账号加入能量池: {} 可用能量: {}", account.getAddress(), account.getAvailableEnergy());
            }
        } finally {
            queueLock.writeLock().unlock();
        }
    }

    @Override
    public void removeFromPool(Long accountId) {
        queueLock.writeLock().lock();
        try {
            EnergyAccount account = accountMap.remove(accountId);
            if (account != null) {
                if(account.getLessorType().compareTo(0) == 0){
                    energyPool.remove(account);
                }else{
                    thirdEnergyPool.remove(account);
                }

                log.info("账号从能量池移除: {}", account.getAddress());
            }
        } finally {
            queueLock.writeLock().unlock();
        }
    }

    /**
     * 获取能量池中所有账号（用于能量恢复检查）
     */
    public List<EnergyAccount> getAllPoolAccounts() {
        queueLock.readLock().lock();
        try {
            return new java.util.ArrayList<>(accountMap.values());
        } finally {
            queueLock.readLock().unlock();
        }
    }

    /**
     * 统计能量池中所有账号的可用能量总和
     */
    @Override
    public long getTotalAvailableEnergy() {
        queueLock.readLock().lock();
        try {
            return accountMap.values().stream()
                    .mapToLong(EnergyAccount::getAvailableEnergy)
                    .sum();
        } finally {
            queueLock.readLock().unlock();
        }
    }

    public EnergyStatsVO getEnergyStatsVO(){
        EnergyStatsVO stats = new EnergyStatsVO();
        queueLock.readLock().lock();
        try {
            stats.setDepositAddress(configService.getValueByKey("depositAddress"));
            stats.setEnergyPoolSize(configService.getLongByKey("energyPoolSize"));
            stats.setAvailableEnergy(configService.getLongByKey("availableEnergy") + getTotalAvailableEnergy());
            stats.setTransactionVolume(configService.getLongByKey("transactionVolume"));
            stats.setUserCount(configService.getLongByKey("userCount"));

        } finally {
            queueLock.readLock().unlock();
        }
        return stats;
    }


    @EventListener
    public void refresh(ReBuildCacheEvent event){
        configService.getByKey("depositAddress", true);
        configService.getByKey("energyPoolSize", true);
        configService.getByKey("availableEnergy", true);
        configService.getByKey("transactionVolume", true);
        configService.getByKey("userCount", true);
        configService.getByKey("energyUnitAmount", true);
        configService.getByKey("rentalUnitTrx", true);
    }

    @EventListener
    public void refreshEnergyPool(ReFreshEnergyPoolEvent event){
        log.info("后台能量池数据修改，刷新能量池");
        initPriorityQueue();
    }


}
