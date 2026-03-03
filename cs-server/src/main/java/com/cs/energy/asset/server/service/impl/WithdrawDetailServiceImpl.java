package com.cs.energy.asset.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cs.energy.asset.api.dto.TxV3SimpleDTO;
import com.cs.energy.asset.api.entity.WithdrawDetail;
import com.cs.energy.asset.api.entity.WithdrawFlow;
import com.cs.energy.asset.api.enums.WithdrawDetailStatusEnum;
import com.cs.energy.asset.api.enums.WithdrawStatusEnum;
import com.cs.energy.asset.api.service.WithdrawDetailService;
import com.cs.energy.asset.api.vo.WithdrawFlowConfirmVO;
import com.cs.energy.asset.server.mapper.WithdrawDetailMapper;
import com.cs.energy.asset.server.mapper.WithdrawFlowMapper;
import com.cs.energy.system.api.enums.SseNameEnum;
import com.cs.energy.system.api.service.SseService;
import com.cs.sp.common.BeanCopior;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author gpthk
 * @since 2024-11-25
 */
@Slf4j
@Service
public class WithdrawDetailServiceImpl extends ServiceImpl<WithdrawDetailMapper, WithdrawDetail> implements WithdrawDetailService {

    @Autowired
    private WithdrawFlowMapper withdrawFlowMapper;

    @Autowired
    private SseService sseService;

    @Override
    public void updateConfirm(WithdrawDetail detail, TxV3SimpleDTO.Transaction tx) {
        WithdrawDetail update = new WithdrawDetail();
        update.setId(detail.getId());
        // 用区块时间
        update.setConfirmAt(tx.getNow() * 1000);
        update.setConfirmBlockNo(tx.getMcBlockSeqno());
        update.setStatus(WithdrawDetailStatusEnum.DONE.getCode());
        // 更新确认，只有确认中的数据才更新
        boolean updated = update(update, Wrappers.lambdaUpdate(WithdrawDetail.class)
                .eq(WithdrawDetail::getId, detail.getId())
                .eq(WithdrawDetail::getStatus, WithdrawDetailStatusEnum.CONFIRMING.getCode())
        );
        if (updated) {
//            SpringUtil.getBean(MultiDeposits.class).removelistener(detail);
            log.info("withdraw detail {} confirmed", detail.getId());
        }
        // 找到提现流水
        WithdrawFlow withdrawFlow = withdrawFlowMapper.selectById(detail.getFlowId());
        if (withdrawFlow == null) {
            return;
        }
        if (WithdrawStatusEnum.DONE.eq(withdrawFlow.getStatus())) {
            return;
        }
        // 确认是否已全部完成
        List<WithdrawDetail> list = list(new QueryWrapper<WithdrawDetail>().lambda()
                .eq(WithdrawDetail::getStatus, WithdrawDetailStatusEnum.DONE.getCode())
                .orderByAsc(WithdrawDetail::getSeq)
        );

        if (list.size() >= withdrawFlow.getTxNum()) {
            WithdrawFlow finish = new WithdrawFlow();
            finish.setId(withdrawFlow.getId());
            finish.setClaimTx(list.stream().map(WithdrawDetail::getHash).collect(Collectors.joining(",")));
            // 第一个发出的时候开始领取
            finish.setClaimAt(list.get(0).getSendAt());
            // 最后一个完成确认的时候确认
            finish.setConfirmAt(update.getConfirmAt());
            finish.setStatus(WithdrawStatusEnum.DONE.getCode());
            int row = withdrawFlowMapper.update(finish, Wrappers.lambdaUpdate(WithdrawFlow.class)
                    .eq(WithdrawFlow::getId, withdrawFlow.getId())
                    .ne(WithdrawFlow::getStatus, WithdrawStatusEnum.DONE.getCode())
            );
            if (row > 0) {
                long sendNum = sseService.sendTo(withdrawFlow.getUid(), SseNameEnum.WITHDRAW_FINISH.getCode(),
                        BeanCopior.map(update, WithdrawFlowConfirmVO.class));
                log.info("sse withdraw finish {} =>{}", SseNameEnum.WITHDRAW_FINISH.getCode(), sendNum);
            }
        }
    }
}
