package com.cs.copy.evm.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cs.copy.evm.api.entity.PriceHis;

import java.math.BigDecimal;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author gpthk
 * @since 2024-12-10
 */
public interface PriceHisService extends IService<PriceHis> {
    public void priceCllect();
    public BigDecimal twap(String token);
}
