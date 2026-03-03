package com.cs.energy.evm.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cs.energy.evm.api.entity.Chain;
import com.cs.energy.evm.server.mapper.ChainMapper;
import com.cs.energy.evm.api.service.ChainService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author gpthk
 * @since 2024-06-01
 */
@Service
public class ChainServiceImpl extends ServiceImpl<ChainMapper, Chain> implements ChainService {

}
