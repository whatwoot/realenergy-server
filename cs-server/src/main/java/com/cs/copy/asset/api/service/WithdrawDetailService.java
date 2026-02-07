package com.cs.copy.asset.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cs.copy.asset.api.dto.TxV3SimpleDTO;
import com.cs.copy.asset.api.entity.WithdrawDetail;

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
