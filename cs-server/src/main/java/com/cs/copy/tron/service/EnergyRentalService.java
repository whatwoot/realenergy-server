package com.cs.copy.tron.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cs.copy.tron.api.vo.EnergyStatsVO;
import com.cs.copy.tron.entity.EnergyRental;

/**
 * 能量租赁记录服务接口
 */
public interface EnergyRentalService extends IService<EnergyRental> {

    /**
     * 初始化租用到期时间优先队列
     */
    void initExpirationQueue();

    /**
     * 添加租赁记录到到期队列
     * @param rental 租赁记录
     */
    void addToExpirationQueue(EnergyRental rental);

    /**
     * 获取最早到期的租赁记录
     * @return 最早到期的租赁记录，如果没有返回null
     */
    EnergyRental peekEarliestExpiration();

    /**
     * 弹出并返回最早到期的租赁记录
     * @return 最早到期的租赁记录
     */
    EnergyRental pollEarliestExpiration();

    /**
     * 从到期队列移除租赁记录
     * @param rentalId 租赁记录ID
     */
    void removeFromExpirationQueue(Long rentalId);

    public Long getLastRentTime();
}
