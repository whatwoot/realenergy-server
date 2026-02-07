package com.cs.copy.evm.api.service;

import com.cs.copy.evm.api.entity.BlockHis;
import com.baomidou.mybatisplus.extension.service.IService;
import org.web3j.protocol.core.methods.response.EthBlock;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author gpthk
 * @since 2024-12-08
 */
public interface BlockHisService extends IService<BlockHis> {

    BlockHis addNew(EthBlock.Block block);

    BlockHis addNew(BlockHis add);
}
