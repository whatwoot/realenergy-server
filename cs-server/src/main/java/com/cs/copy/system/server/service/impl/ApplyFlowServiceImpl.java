package com.cs.copy.system.server.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cs.copy.asset.api.service.AssetService;
import com.cs.copy.system.api.entity.ApplyFlow;
import com.cs.copy.system.api.enums.ApplyFlowStatusEnum;
import com.cs.copy.system.api.service.ApplyFlowService;
import com.cs.copy.system.api.service.ConfigService;
import com.cs.copy.system.server.mapper.ApplyFlowMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.cs.sp.common.WebAssert.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author gpthk
 * @since 2025-10-09
 */
@Slf4j
@Service
public class ApplyFlowServiceImpl extends ServiceImpl<ApplyFlowMapper, ApplyFlow> implements ApplyFlowService {

    @Autowired
    private AssetService assetService;
    @Autowired
    private ConfigService configService;

    @Override
    public void updateAudit(ApplyFlow req) {
        ApplyFlow applyFlow = getById(req.getId());
        isNotNull(applyFlow, "chk.common.invalid", "id");
        // 忽略状态可以重新审核
        ApplyFlowStatusEnum statusEnum = ApplyFlowStatusEnum.of(req.getStatus());
        // 原来是忽略状态，可以通过或拒绝
        if (ApplyFlowStatusEnum.IGNORE.eq(applyFlow.getStatus())) {
            expect(ApplyFlowStatusEnum.AUDITED.equals(statusEnum) || ApplyFlowStatusEnum.REJECT.equals(statusEnum),
                    "chk.common.invalid", "status");
        } else if (ApplyFlowStatusEnum.NOT_AUDIT.eq(applyFlow.getStatus())) {
            // 待审核，只能通过或拒绝
            isTrue(ApplyFlowStatusEnum.AUDITED.equals(statusEnum) || ApplyFlowStatusEnum.REJECT.equals(statusEnum)
                    || ApplyFlowStatusEnum.IGNORE.equals(statusEnum), "chk.common.invalid", "status");
        } else {
            throwBizException("chk.apply.alreadyAudit");
        }

        ApplyFlow update = new ApplyFlow();
        update.setId(req.getId());
        update.setStatus(req.getStatus());
        // 忽略不做审核
        if (!ApplyFlowStatusEnum.IGNORE.eq(req.getStatus())) {
            update.setAuditAt(System.currentTimeMillis());
        }
        update.setAuditMsg(req.getAuditMsg());
        int row = getBaseMapper().update(update, Wrappers.lambdaUpdate(ApplyFlow.class)
                .eq(ApplyFlow::getId, req.getId())
                .in(ApplyFlow::getStatus,
                        ApplyFlowStatusEnum.NOT_AUDIT.getCode(),
                        ApplyFlowStatusEnum.IGNORE.getCode()
                )
        );

        if (row > 0) {
            switch (statusEnum) {
                case AUDITED:
//                    approve(applyFlow, req);
                    break;
                case REJECT:
                    reject(applyFlow, req);
                    break;
                case IGNORE:
                    // 无须操作，只是为了审核列表不展示已处理的
                    break;
                default:
                    break;
            }
        }
    }

    private void reject(ApplyFlow applyFlow, ApplyFlow req) {
//        String params = applyFlow.getParams();
//        JSONArray ids = JSONArray.parseArray(params);
//        Long id;
//        InvestFlow investFlow;
//        int row;
//        for (int i = 0; i < ids.size(); i++) {
//            id = ids.getLong(i);
//            investFlow = new InvestFlow();
//            investFlow.setSold(InvestFlowSoldEnum.NOT_SELL.getCode());
//            row = investFlowMapper.update(investFlow, Wrappers.lambdaQuery(InvestFlow.class)
//                    .eq(InvestFlow::getId, id)
//                    .eq(InvestFlow::getSold, InvestFlowSoldEnum.AUDIT.getCode())
//            );
//            log.info("Invest-reject {},  {} res: {}", req.getId(), id, row);
//        }
    }


}
