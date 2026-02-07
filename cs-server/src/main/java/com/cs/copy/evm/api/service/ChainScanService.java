package com.cs.copy.evm.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cs.copy.evm.api.entity.ChainScan;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author gpthk
 * @since 2024-06-03
 */
public interface ChainScanService extends IService<ChainScan> {

    List<ChainScan> listBscAndCache();

    List<ChainScan> listBscAndCache(boolean force);
}
