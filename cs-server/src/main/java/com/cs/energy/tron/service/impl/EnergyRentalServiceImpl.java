package com.cs.energy.tron.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cs.energy.tron.entity.EnergyRental;
import com.cs.energy.tron.mapper.EnergyRentalMapper;
import com.cs.energy.tron.service.EnergyRentalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 能量租赁记录服务实现
 * 使用优先队列管理租赁到期时间，按到期时间升序排列
 */
@Slf4j
@Service("tronV2EnergyRentalService")
public class EnergyRentalServiceImpl extends ServiceImpl<EnergyRentalMapper, EnergyRental> implements EnergyRentalService {

    /**
     * 租赁到期时间优先队列 - 按到期时间升序排列（最早到期的在队首）
     */
    private final PriorityBlockingQueue<EnergyRental> expirationQueue = new PriorityBlockingQueue<>(
            100,
            Comparator.comparingLong(EnergyRental::getExpireAt)
    );

    /**
     * 租赁ID到租赁对象的映射，用于快速查找
     */
    private final ConcurrentHashMap<Long, EnergyRental> rentalMap = new ConcurrentHashMap<>();

    /**
     * 读写锁，保护优先队列操作
     */
    private final ReentrantReadWriteLock queueLock = new ReentrantReadWriteLock();

    @PostConstruct
    public void init() {
        initExpirationQueue();
    }

    @Override
    public void initExpirationQueue() {
        queueLock.writeLock().lock();
        try {
            expirationQueue.clear();
            rentalMap.clear();

            // 从数据库加载所有活跃的租赁记录
            List<EnergyRental> rentals = list(new LambdaQueryWrapper<EnergyRental>()
                    .eq(EnergyRental::getStatus, 1)); // 状态1: 活跃

            for (EnergyRental rental : rentals) {
                expirationQueue.offer(rental);
                rentalMap.put(rental.getId(), rental);
            }

            log.info("租赁到期队列初始化完成，共加载 {} 条活跃租赁", rentals.size());
        } finally {
            queueLock.writeLock().unlock();
        }
    }

    @Override
    public void addToExpirationQueue(EnergyRental rental) {
        queueLock.writeLock().lock();
        try {
            // 确保不重复添加
            EnergyRental existing = rentalMap.get(rental.getId());
            if (existing != null) {
                expirationQueue.remove(existing);
            }
            expirationQueue.offer(rental);
            rentalMap.put(rental.getId(), rental);
            log.debug("租赁记录加入到期队列: ID={} 到期时间={}", rental.getId(), rental.getExpireAt());
        } finally {
            queueLock.writeLock().unlock();
        }
    }

    @Override
    public EnergyRental peekEarliestExpiration() {
        queueLock.readLock().lock();
        try {
            return expirationQueue.peek();
        } finally {
            queueLock.readLock().unlock();
        }
    }

    @Override
    public EnergyRental pollEarliestExpiration() {
        queueLock.writeLock().lock();
        try {
            EnergyRental rental = expirationQueue.poll();
            if (rental != null) {
                rentalMap.remove(rental.getId());
            }
            return rental;
        } finally {
            queueLock.writeLock().unlock();
        }
    }

    @Override
    public void removeFromExpirationQueue(Long rentalId) {
        queueLock.writeLock().lock();
        try {
            EnergyRental rental = rentalMap.remove(rentalId);
            if (rental != null) {
                expirationQueue.remove(rental);
                log.debug("租赁记录从到期队列移除: ID={}", rentalId);
            }
        } finally {
            queueLock.writeLock().unlock();
        }
    }

    /**
     * 检查交易Hash是否已处理
     */
    public boolean isRequestTxProcessed(String txHash) {
        return count(new LambdaQueryWrapper<EnergyRental>()
                .eq(EnergyRental::getRequestTxHash, txHash)) > 0;
    }

    /**
     * 获取最后一次rent的时间戳
     */
    public Long getLastRentTime() {
        EnergyRental rental = getOne(new LambdaQueryWrapper<EnergyRental>()
                .orderByDesc(EnergyRental::getCreateAt)
                .last("LIMIT 1"));
        return rental != null ? rental.getCreateAt() : 0L;
    }

}
