package com.cs.copy.tron.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cs.copy.tron.api.vo.EnergyStatsVO;
import com.cs.copy.tron.entity.EnergyAccount;

/**
 * 能量池账号服务接口
 */
public interface EnergyAccountService extends IService<EnergyAccount> {

    /**
     * 初始化能量池优先队列
     */
    void initPriorityQueue();

    /**
     * 获取可用能量最大的账号
     * @param minEnergy 最小能量需求
     * @return 能量账号，如果没有满足条件的返回null
     */
    EnergyAccount pollMaxAvailableAccount(long minEnergy);

    /**
     * 更新账号可用能量并重新排序
     * @param account 能量账号
     */
    void updateAvailableEnergy(EnergyAccount account);

    /**
     * 添加账号到能量池
     * @param account 能量账号
     */
    void addToPool(EnergyAccount account);

    /**
     * 从能量池移除账号
     * @param accountId 账号ID
     */
    void removeFromPool(Long accountId);

    /**
     * 统计能量池中所有账号的可用能量总和
     * @return 可用能量总和
     */
    long getTotalAvailableEnergy();

    public EnergyStatsVO getEnergyStatsVO();
}
