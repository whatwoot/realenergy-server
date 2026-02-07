package com.cs.copy.evm.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cs.copy.asset.server.mapper.AssetFlowMapper;
import com.cs.copy.evm.api.entity.ChainWork;
import com.cs.copy.evm.api.service.ChainWorkService;
import com.cs.copy.evm.server.mapper.ChainWorkMapper;
import com.cs.copy.member.server.mapper.LoginMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author gpthk
 * @since 2024-12-21
 */
@Slf4j
@Service
public class ChainWorkServiceImpl extends ServiceImpl<ChainWorkMapper, ChainWork> implements ChainWorkService {

    @Autowired
    private LoginMapper loginMapper;

    @Autowired
    private AssetFlowMapper assetFlowMapper;

    @Override
    public void updateStake(ChainWork chainWork) {

    }

    @Override
    public void updateUnStake(ChainWork chainWork) {

    }
}
