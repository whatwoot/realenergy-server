package com.cs.energy.thd.server.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cs.energy.asset.api.dto.WithdrawParamDTO;
import com.cs.energy.asset.api.entity.AssetFlow;
import com.cs.energy.asset.api.entity.WithdrawFlow;
import com.cs.energy.asset.api.enums.AssetSceneEnum;
import com.cs.energy.asset.api.enums.WithdrawStatusEnum;
import com.cs.energy.asset.api.service.AssetService;
import com.cs.energy.asset.server.mapper.WithdrawFlowMapper;
import com.cs.energy.global.constants.CacheKey;
import com.cs.energy.member.api.enums.ChainEnum;
import com.cs.energy.system.api.dto.GlobalConfigDTO;
import com.cs.energy.system.api.service.ConfigService;
import com.cs.energy.system.server.config.prop.AppProperties;
import com.cs.energy.thd.api.entity.PayFlow;
import com.cs.energy.thd.api.enums.MerchantPaymentTypeEnum;
import com.cs.energy.thd.api.enums.PayFlowPayStatusEnum;
import com.cs.energy.thd.api.enums.PayFlowStatusEnum;
import com.cs.energy.thd.api.request.HhffCallRequest;
import com.cs.energy.thd.api.request.hhff.OrderRes;
import com.cs.energy.thd.api.request.hhff.Pay;
import com.cs.energy.thd.api.request.hhff.PayQuery;
import com.cs.energy.thd.api.service.HhffService;
import com.cs.energy.thd.server.config.HhffHelper;
import com.cs.energy.thd.server.mapper.PayFlowMapper;
import com.cs.sp.enums.YesNoByteEnum;
import com.cs.sp.enums.YesNoIntEnum;
import com.cs.web.spring.helper.RedisIdWorker;
import com.cs.web.spring.helper.RedissonLockHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.cs.sp.common.WebAssert.*;


/**
 * @authro fun
 * @date 2025/4/1 22:30
 */
@Slf4j
@Service
public class HhffServiceImpl implements HhffService {
    @Autowired
    private HhffHelper hhffHelper;

    @Autowired
    private RedissonLockHelper redissonLockHelper;

    @Autowired
    private PayFlowMapper payFlowMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private AppProperties appProperties;

    @Autowired
    private AssetService assetService;

    @Autowired
    private WithdrawFlowMapper withdrawFlowMapper;

    @Autowired
    private ConfigService configService;

    @Autowired
    private RedisIdWorker redisIdWorker;


    /**
     * 扫描cny提现
     */
    //@Scheduled(fixedDelay = 5000L, initialDelay = 5000L)
    public void scanCnyWithdraw() {
        GlobalConfigDTO globalConfig = configService.getGlobalConfig(GlobalConfigDTO.class);
        if (!YesNoIntEnum.YES.eq(globalConfig.getWithdrawFlag())) {
            return;
        }
        try {
            doWithdraw();
        } catch (Throwable e) {
            log.warn("Withdraw-CNY failed", e);
        }
    }

    private void doWithdraw() {
        List<WithdrawFlow> flows = withdrawFlowMapper.selectList(Wrappers.lambdaQuery(WithdrawFlow.class)
                .eq(WithdrawFlow::getChain, ChainEnum.CNY.getCode())
                .eq(WithdrawFlow::getAuditStatus, YesNoByteEnum.YES.getCode())
                .eq(WithdrawFlow::getStatus, WithdrawStatusEnum.COMMIT.getCode())
                .orderByAsc(WithdrawFlow::getId)
                .last("limit 1")
        );
        if (flows.isEmpty()) {
            return;
        }
        WithdrawFlow flow = flows.get(0);

        List<PayFlow> payFlows = payFlowMapper.selectList(Wrappers.lambdaQuery(PayFlow.class)
                .eq(PayFlow::getRelateId, flow.getId())
                .orderByDesc(PayFlow::getId)
        );
        WithdrawParamDTO paramsDTO = JSONObject.parseObject(flow.getParams(), WithdrawParamDTO.class);
        // 如果没有
        if (payFlows.isEmpty()) {
            doPay(flow, paramsDTO);
        } else {
            PayFlow payFlow = payFlows.get(0);
            if (PayFlowStatusEnum.CHECK.eq(payFlow.getStatus())) {
                doCheckUion(payFlow, paramsDTO, WithdrawStatusEnum.TRANSFERING);
            }
        }
    }

    private void doPay(WithdrawFlow flow, WithdrawParamDTO paramsDTO) {
        // 先标记开始，防止重启，但是接口又调用了
        PayFlow payFlow = startCall(flow, paramsDTO);
        PayFlow done = new PayFlow();
        done.setId(payFlow.getId());

        Pay pay = new Pay();
        pay.setAmount(payFlow.getAmount().stripTrailingZeros().toPlainString());
        pay.setDistributeCode(payFlow.getId());
        pay.setPaytool(appProperties.getPaytool());
        done.setPayChannel(pay.getPaytool());
        if (MerchantPaymentTypeEnum.ALIPAY.eq(paramsDTO.getType())) {
            pay.setBank("支付宝");
        }
        pay.setCardnumber(paramsDTO.getAccount());
        pay.setCardholder(paramsDTO.getName());
        pay.setPaymentcode(paramsDTO.getCode());
        String res = null;
        try {
            res = hhffHelper.pay(appProperties.getPayNotifyUrl(), pay);
            done.setResp(StringUtils.truncate(res, 480));
            // 响应结果不可信，报失败也会创建成功订单
        } catch (Throwable e) {
            log.warn("hhff-pay failed", e);
            // 这种要回查接口
            done.setResp(StringUtils.truncate(e.getMessage(), 480));
        }
        done.setStatus(PayFlowStatusEnum.CHECK.getCode());
        payFlowMapper.updateById(done);
        // 更新完立即检查状态。此时是start状态
        doCheckUion(payFlow, paramsDTO, WithdrawStatusEnum.TRANSFERING);
    }

    /**
     * 1、刚提交订单后，检查
     * 2、检查状态，复检
     * 3、回调时，检查
     *
     * @param paramFlow
     * @param dto
     */
    private void doCheckUion(PayFlow paramFlow, WithdrawParamDTO dto, WithdrawStatusEnum startStatus) {
        String key = CacheKey.THD_PAY_LOCK + paramFlow.getId();
        redissonLockHelper.withLock(key, 60, 120, TimeUnit.SECONDS, () -> {
            PayFlow payFlow = payFlowMapper.selectById(paramFlow.getId());
            if (payFlow == null) {
                return;
            }
            // 已完成
            if (PayFlowStatusEnum.OK.eq(payFlow.getStatus()) || PayFlowStatusEnum.FAIL.eq(payFlow.getStatus())) {
                return;
            }
            WithdrawParamDTO paramsDTO;
            if (dto != null) {
                paramsDTO = dto;
            } else {
                WithdrawFlow withdrawFlow = withdrawFlowMapper.selectById(payFlow.getRelateId());
                paramsDTO = JSONObject.parseObject(withdrawFlow.getParams(), WithdrawParamDTO.class);
            }
            //
            PayFlow done = new PayFlow();
            done.setId(payFlow.getId());
            OrderRes queryRes = null;
            try {
                PayQuery query = new PayQuery();
                query.setDistributeCode(payFlow.getId());
                queryRes = hhffHelper.query(query);
                if (Arrays.asList(HhffHelper.STATUS_NEW, HhffHelper.STATUS_ING).contains(queryRes.getStatus())) {
                    done.setStatus(PayFlowStatusEnum.WAIT_CALLBACK.getCode());
                } else if (HhffHelper.STATUS_OK.equals(queryRes.getStatus())) {
                    done.setNotifyAt(System.currentTimeMillis());
                    done.setStatus(PayFlowStatusEnum.OK.getCode());
                    done.setPayStatus(PayFlowPayStatusEnum.PAYED.getCode());
                } else if (HhffHelper.STATUS_FAIL.equals(queryRes.getStatus())) {
                    // 复查订单失败，则是失败
                    done.setStatus(PayFlowStatusEnum.FAIL.getCode());
                    done.setFallbackAt(System.currentTimeMillis());
                    done.setPayStatus(PayFlowPayStatusEnum.FALLBACK.getCode());
                } else if (queryRes.getError() != null) {
                    done.setFallbackAt(System.currentTimeMillis());
                    if (queryRes.getError().getCode().equals(HhffHelper.NO_ORDER_ERROR)) {
                        // 没订单也算失败
                        done.setStatus(PayFlowStatusEnum.FAIL.getCode());
                        done.setPayStatus(PayFlowPayStatusEnum.FALLBACK.getCode());
//                    {"error":{"code":"1010404","desc":"订单不存在: 223774479090718972"}}
                    } else {
                        done.setStatus(PayFlowStatusEnum.OTHER.getCode());
                    }
                } else {
                    done.setStatus(PayFlowStatusEnum.CHECK.getCode());
                }
            } catch (Throwable e) {
                done.setStatus(PayFlowStatusEnum.CHECK.getCode());
            }

            // 如果原来状态跟新状态一样，则不做处理
            if (done.getStatus().equals(payFlow.getStatus())) {
                return;
            }

            transactionTemplate.execute(tx -> {
                // 改状态
                int row = payFlowMapper.update(done, Wrappers.lambdaUpdate(PayFlow.class)
                        .eq(PayFlow::getId, done.getId())
                        .eq(PayFlow::getStatus, payFlow.getStatus())
                        .ne(PayFlow::getStatus, PayFlowStatusEnum.FAIL.getCode())
                );
                if (row > 0) {
                    PayFlowStatusEnum statusEnum = PayFlowStatusEnum.of(done.getStatus());
                    switch (statusEnum) {
                        case OK:
                            WithdrawFlow withdrawOk = new WithdrawFlow();
                            withdrawOk.setId(payFlow.getRelateId());
                            withdrawOk.setStatus(WithdrawStatusEnum.DONE.getCode());
                            withdrawOk.setConfirmAt(System.currentTimeMillis());
                            withdrawOk.setClaimTx(payFlow.getId().toString());
                            row = withdrawFlowMapper.update(withdrawOk, Wrappers.lambdaUpdate(WithdrawFlow.class)
                                    .eq(WithdrawFlow::getId, withdrawOk.getId())
                                    .ne(WithdrawFlow::getStatus, WithdrawStatusEnum.REFUND.getCode())
                            );
                            log.info("Withdraw-cny-ok {} of{} {}({}) ing {}", payFlow.getId(), payFlow.getRelateId(),
                                    paramsDTO.getAccount(), paramsDTO.getName(), row);
                            if (row <= 0) {

                            }
                            break;
                        case WAIT_CALLBACK:
                            // 如果等回调就表示成功
                            WithdrawFlow update = new WithdrawFlow();
                            update.setId(payFlow.getRelateId());
                            update.setStatus(WithdrawStatusEnum.CONFIRMING.getCode());
                            row = withdrawFlowMapper.update(update, Wrappers.lambdaUpdate(WithdrawFlow.class)
                                    .eq(WithdrawFlow::getId, update.getId())
                                    .eq(WithdrawFlow::getStatus, startStatus.getCode())
                            );
                            log.info("Withdraw-cny-wait {} of {}  {}({}) ing {}", payFlow.getId(), payFlow.getRelateId(),
                                    paramsDTO.getAccount(), paramsDTO.getName(), row);
                            break;
                        case CHECK:
                            // 如果是待复查，就等下一轮检查
                            WithdrawFlow restore = new WithdrawFlow();
                            restore.setId(payFlow.getRelateId());
                            restore.setStatus(WithdrawStatusEnum.COMMIT.getCode());
                            row = withdrawFlowMapper.update(restore, Wrappers.lambdaUpdate(WithdrawFlow.class)
                                    .eq(WithdrawFlow::getId, restore.getId())
                                    .eq(WithdrawFlow::getStatus, startStatus.getCode())
                            );
                            log.info("Withdraw-cny-recheck {} of{}  {}({}) ing {}", payFlow.getId(), payFlow.getRelateId(),
                                    paramsDTO.getAccount(), paramsDTO.getName(), row);
                            break;
                        case FAIL:
                            // 如果明确失败，则退款
                            WithdrawFlow refund = new WithdrawFlow();
                            refund.setId(payFlow.getRelateId());
                            refund.setStatus(WithdrawStatusEnum.REFUND.getCode());
                            row = withdrawFlowMapper.update(refund, Wrappers.lambdaUpdate(WithdrawFlow.class)
                                    .eq(WithdrawFlow::getId, refund.getId())
                                    .ne(WithdrawFlow::getStatus, WithdrawStatusEnum.REFUND.getCode())
                            );
                            if (row > 0) {
                                AssetFlow refundFlow = new AssetFlow();
                                refundFlow.setRelateId(payFlow.getRelateId());
                                MerchantPaymentTypeEnum typeEnum = MerchantPaymentTypeEnum.of(paramsDTO.getType());
                                switch (typeEnum) {
                                    case WX:
                                        refundFlow.setScene(AssetSceneEnum.WITHDRAW_WECHAT.getCode());
                                        break;
                                    case ALIPAY:
                                        refundFlow.setScene(AssetSceneEnum.WITHDRAW_ALIPAY.getCode());
                                        break;
                                    case UNIONPAY:
                                        refundFlow.setScene(AssetSceneEnum.WITHDRAW_UNIONPAY.getCode());
                                        break;
                                    default:
                                        break;
                                }
                                assetService.updateRefundOptional(refundFlow);
                                log.info("Withdraw-cny-refund {} of {}  {}({}) ing {}", payFlow.getId(), payFlow.getRelateId(),
                                        paramsDTO.getAccount(), paramsDTO.getName(), row);
                            } else {
                                log.warn("Withdraw-cny-refund-already {} of {}  {}({}) ing {}", payFlow.getId(), payFlow.getRelateId(),
                                        paramsDTO.getAccount(), paramsDTO.getName(), row);
                            }
                            break;
                        default:
                            break;
                    }
                }
                return null;
            });
        });
    }


    private PayFlow startCall(WithdrawFlow flow, WithdrawParamDTO paramsDTO) {
        PayFlow payFlow = new PayFlow();
        payFlow.setId(redisIdWorker.nextId(CacheKey.PAY_FLOW_ID));
        payFlow.setProvider("hhff");
        payFlow.setScene("0101");
        payFlow.setRelateId(flow.getId());
        payFlow.setUid(flow.getUid());
        // 实际到的rmb数量
        payFlow.setAmount(flow.getArriveQuantity());
        // 实际提的代币数量
        payFlow.setQuantity(flow.getArriveValue());
        payFlow.setCreateAt(System.currentTimeMillis());
        payFlow.setStatus(PayFlowStatusEnum.START.getCode());
        payFlow.setPayMode(MerchantPaymentTypeEnum.of(paramsDTO.getType()).getMsg());
        // 更新状态和准备调用接口必须在一个事务
        return transactionTemplate.execute(tx -> {
            payFlowMapper.insert(payFlow);
            WithdrawFlow toTransfer = new WithdrawFlow();
            toTransfer.setId(flow.getId());
            toTransfer.setStatus(WithdrawStatusEnum.TRANSFERING.getCode());
            int row = withdrawFlowMapper.update(toTransfer,
                    Wrappers.lambdaQuery(WithdrawFlow.class)
                            .eq(WithdrawFlow::getId, toTransfer.getId())
                            .eq(WithdrawFlow::getStatus, WithdrawStatusEnum.COMMIT.getCode()));
            expectGt0(row, "chk.withdraw.launchFail");
            return payFlow;
        });
    }


    @Override
    public boolean onNotify(HhffCallRequest req) {
        return onNotify(req, false);
    }

    @Override
    public boolean onNotify(HhffCallRequest req, boolean notSign) {
        log.info("PayFlow-notify sign: {}, {}", !notSign, JSONObject.toJSONString(req));
        boolean signEq = hhffHelper.verifySign(req);
        if (!notSign) {
            expect(signEq, "chk.hhff.signFail");
        }
        PayFlow payFlow = new PayFlow();
        payFlow.setId(req.getDistributeCode());
        doCheckUion(payFlow, null, null);
        return true;
    }


    @Override
    public void updateToOk(Long id) {
        PayFlow payFlow = payFlowMapper.selectById(id);
        expectNotNull(payFlow, "chk.common.invalid", "id");
        expect(!PayFlowStatusEnum.FAIL.eq(payFlow.getStatus()), "该订单已退款");
        //
        PayFlow done = new PayFlow();
        done.setNotifyAt(System.currentTimeMillis());
        done.setStatus(PayFlowStatusEnum.OK.getCode());
        done.setPayStatus(PayFlowPayStatusEnum.PAYED.getCode());
        int row = payFlowMapper.update(done, Wrappers.lambdaUpdate(PayFlow.class)
                .eq(PayFlow::getId, payFlow.getId())
                .ne(PayFlow::getStatus, PayFlowStatusEnum.FAIL.getCode())
        );
        if (row > 0) {
            WithdrawFlow withdrawOk = new WithdrawFlow();
            withdrawOk.setId(payFlow.getRelateId());
            withdrawOk.setStatus(WithdrawStatusEnum.DONE.getCode());
            withdrawOk.setConfirmAt(System.currentTimeMillis());
            withdrawOk.setClaimTx(payFlow.getId().toString());
            row = withdrawFlowMapper.update(withdrawOk, Wrappers.lambdaUpdate(WithdrawFlow.class)
                    .eq(WithdrawFlow::getId, withdrawOk.getId())
                    .ne(WithdrawFlow::getStatus, WithdrawStatusEnum.REFUND.getCode())
            );
            log.info("Pay-flow-ok {}, {}", payFlow.getId(), row);
        }
    }

    @Override
    public void updateToRefund(Long id) {
        PayFlow payFlow = payFlowMapper.selectById(id);
        expectNotNull(payFlow, "chk.common.invalid", "id");
        expect(!PayFlowStatusEnum.FAIL.eq(payFlow.getStatus()), "该订单已退款");

        WithdrawFlow withdrawRefund = withdrawFlowMapper.selectById(payFlow.getRelateId());
        WithdrawParamDTO paramsDTO = JSONObject.parseObject(withdrawRefund.getParams(), WithdrawParamDTO.class);

        PayFlow done = new PayFlow();
        done.setStatus(PayFlowStatusEnum.FAIL.getCode());
        done.setPayStatus(PayFlowPayStatusEnum.FALLBACK.getCode());
        done.setFallbackAt(System.currentTimeMillis());
        int row = payFlowMapper.update(done, Wrappers.lambdaUpdate(PayFlow.class)
                .eq(PayFlow::getId, payFlow.getId())
                .ne(PayFlow::getStatus, PayFlowStatusEnum.FAIL.getCode())
        );
        expectGt0(row, "该订单已退款");

        WithdrawFlow refund = new WithdrawFlow();
        refund.setId(payFlow.getRelateId());
        refund.setStatus(WithdrawStatusEnum.REFUND.getCode());
        row = withdrawFlowMapper.update(refund, Wrappers.lambdaUpdate(WithdrawFlow.class)
                .eq(WithdrawFlow::getId, refund.getId())
                .ne(WithdrawFlow::getStatus, WithdrawStatusEnum.REFUND.getCode())
        );
        if (row > 0) {
            AssetFlow refundFlow = new AssetFlow();
            refundFlow.setRelateId(payFlow.getRelateId());
            MerchantPaymentTypeEnum typeEnum = MerchantPaymentTypeEnum.of(paramsDTO.getType());
            switch (typeEnum) {
                case WX:
                    refundFlow.setScene(AssetSceneEnum.WITHDRAW_WECHAT.getCode());
                    break;
                case ALIPAY:
                    refundFlow.setScene(AssetSceneEnum.WITHDRAW_ALIPAY.getCode());
                    break;
                case UNIONPAY:
                    refundFlow.setScene(AssetSceneEnum.WITHDRAW_UNIONPAY.getCode());
                    break;
                default:
                    break;
            }
            assetService.updateRefundOptional(refundFlow);
            log.info("Pay-flow-refund {} of {}  {}({}) ing {}", payFlow.getId(), payFlow.getRelateId(),
                    paramsDTO.getAccount(), paramsDTO.getName(), row);
        } else {
            log.warn("Pay-flow-refund-already {} of {}  {}({}) ing {}", payFlow.getId(), payFlow.getRelateId(),
                    paramsDTO.getAccount(), paramsDTO.getName(), row);
        }
    }
}
