package com.cs.copy.evm.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cs.copy.evm.api.entity.BlockHis;
import com.cs.copy.evm.api.service.BlockHisService;
import com.cs.copy.evm.server.mapper.BlockHisMapper;
import com.cs.sp.enums.YesNoByteEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.response.EthBlock;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author gpthk
 * @since 2024-12-08
 */
@Slf4j
@Service
public class BlockHisServiceImpl extends ServiceImpl<BlockHisMapper, BlockHis> implements BlockHisService {

    @Override
    public BlockHis addNew(EthBlock.Block block) {
        BlockHis blockHis = new BlockHis();
        blockHis.setBlockNo(block.getNumber().longValue());
        blockHis.setBlockHash(block.getHash());
        blockHis.setBlockTime(block.getTimestamp().longValue() * 1000);
        blockHis.setProcessed(YesNoByteEnum.NO.getCode());
        save(blockHis);
        return blockHis;
    }

    @Override
    public BlockHis addNew(BlockHis add) {
        add.setProcessed(YesNoByteEnum.NO.getCode());
//        save(add);
        return add;
    }
}
