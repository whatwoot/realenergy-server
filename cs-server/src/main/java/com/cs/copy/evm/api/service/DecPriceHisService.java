package com.cs.copy.evm.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cs.copy.evm.api.entity.DecPriceHis;

import java.io.IOException;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author gpthk
 * @since 2026-01-04
 */
public interface DecPriceHisService extends IService<DecPriceHis> {

    void scanHis() throws IOException;
}
