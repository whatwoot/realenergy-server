package com.cs.energy.asset.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cs.energy.asset.api.dto.TxV3SimpleDTO;
import com.cs.energy.asset.api.entity.WithdrawDetail;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author gpthk
 * @since 2024-11-25
 */
public interface WithdrawDetailService extends IService<WithdrawDetail> {

    void updateConfirm(WithdrawDetail detail, TxV3SimpleDTO.Transaction tx);
}
