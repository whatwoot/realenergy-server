package com.cs.copy.tron.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cs.copy.system.api.service.ConfigService;
import com.cs.copy.tron.api.vo.EnergyStatsVO;
import com.cs.copy.tron.entity.EnergyAccount;
import com.cs.copy.tron.mapper.EnergyAccountMapper;
import com.cs.copy.tron.service.EnergyAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

            // 从数据库加载所有活跃的能量账号
            List<EnergyAccount> accounts = list(new LambdaQueryWrapper<EnergyAccount>()
                    .eq(EnergyAccount::getStatus, 1));

            for (EnergyAccount account : accounts) {
                energyPool.offer(account);
                accountMap.put(account.getId(), account);
            }

            log.info("能量池优先队列初始化完成，共加载 {} 个账号", accounts.size());
        } finally {
            queueLock.writeLock().unlock();
        }
    }

    @Override
    public EnergyAccount pollMaxAvailableAccount(long minEnergy) {
        queueLock.writeLock().lock();
        try {
            // 查看队首元素
            EnergyAccount top = energyPool.peek();
            if (top == null || top.getAvailableEnergy() < minEnergy) {
                return null;
            }

            // 弹出并返回
            EnergyAccount account = energyPool.poll();
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
            // 如果账号在映射中，先从队列移除
            EnergyAccount existing = accountMap.get(account.getId());
            if (existing != null) {
                energyPool.remove(existing);
            }

            // 更新数据库
            account.setUpdateAt(System.currentTimeMillis());
            updateById(account);

            // 如果账号状态为活跃，重新加入队列
            if (account.getStatus() != null && account.getStatus() == 1) {
                energyPool.offer(account);
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
            if (account.getStatus() != null && account.getStatus() == 1) {
                // 确保不重复添加
                EnergyAccount existing = accountMap.get(account.getId());
                if (existing != null) {
                    energyPool.remove(existing);
                }
                energyPool.offer(account);
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
                energyPool.remove(account);
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


}
