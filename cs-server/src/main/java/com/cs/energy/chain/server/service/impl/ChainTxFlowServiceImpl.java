package com.cs.energy.chain.server.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cs.energy.asset.api.entity.AssetFlow;
import com.cs.energy.asset.api.enums.AssetSceneEnum;
import com.cs.energy.asset.api.service.AssetService;
import com.cs.energy.chain.api.entity.ChainTxFlow;
import com.cs.energy.chain.api.enums.ChainTxFlowStatusEnum;
import com.cs.energy.chain.api.service.ChainTxFlowService;
import com.cs.energy.chain.server.mapper.ChainTxFlowMapper;
import com.cs.energy.member.api.entity.Member;
import com.cs.energy.member.server.mapper.MemberMapper;
import com.cs.sp.enums.YesNoByteEnum;
import com.cs.sp.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author gpthk
 * @since 2024-10-24
 */
@Slf4j
@Service
public class ChainTxFlowServiceImpl extends ServiceImpl<ChainTxFlowMapper, ChainTxFlow> implements ChainTxFlowService {

    @Autowired
    private AssetService assetService;

    @Autowired
    private MemberMapper memberMapper;


    @Override
    public int addDeposits(ChainTxFlow flow) {
        // 需要memo是数字
        if (!StringUtils.isNumber(flow.getMemo())) {
            return 0;
        }
        //
        ChainTxFlow exists = getBaseMapper().selectOne(new QueryWrapper<ChainTxFlow>().lambda()
                .eq(ChainTxFlow::getHash, flow.getHash())
                .eq(ChainTxFlow::getNonce, flow.getNonce())
        );
        if (exists != null) {
            return 0;
        }
        try {
            // 1个区块确认
            flow.setStatus(ChainTxFlowStatusEnum.CONFIRMED.getCode());
            Member member = memberMapper.selectOne(new QueryWrapper<Member>().lambda()
                    .eq(Member::getId, Long.parseLong(flow.getMemo()))
            );
            flow.setReceiptStatus(member != null ? YesNoByteEnum.YES.getCode() : YesNoByteEnum.NO.getCode());
            int row = getBaseMapper().insert(flow);
            // 如果没找到用户
            if (!YesNoByteEnum.YES.eq(flow.getReceiptStatus())) {
                log.warn("Member missing {}", flow.getFromAddr());
                return 0;
            }
//            List<Exchange> exchanges = exchangeService.listAndCached();
//            Optional<Exchange> first = exchanges.stream()
//                    .filter(item -> YesNoByteEnum.YES.eq(item.getStatus()))
//                    .filter(item -> item.getPrice().compareTo(flow.getValue()) == 0).findFirst();
//            if (!first.isPresent()) {
//                log.warn("Deposits invalid {} => {}", flow.getHash(), flow.getValue().stripTrailingZeros().toPlainString());
//                return 0;
//            }
//            Exchange exchange = first.get();
            AssetFlow assetFlow = new AssetFlow();
            assetFlow.setScene(AssetSceneEnum.RECHARGE.getCode());
            assetFlow.setUid(member.getId());
//            assetFlow.setBalance(exchange.getQuantity());
//            assetFlow.setSymbol(exchange.getBaseCurrency());
            assetFlow.setYmd(DateUtil.getYmd());
            assetFlow.setMemo(flow.getHash());
            assetService.updateAsset(assetFlow);
            // 发布充值事件
//            SpringUtil.publishEvent(new DepositeEvent(this, assetFlow));
            return row;
        } catch (DuplicateKeyException e) {
            log.info("Deposits exists {}", flow.getHash());
        }
        return 0;
    }
}
