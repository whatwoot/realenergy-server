package com.cs.energy.asset.server.service.impl;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cs.energy.asset.api.entity.Asset;
import com.cs.energy.asset.api.entity.AssetFlow;
import com.cs.energy.asset.api.entity.WithdrawFlow;
import com.cs.energy.asset.api.enums.AssetSceneEnum;
import com.cs.energy.asset.api.enums.AssetTypeEnum;
import com.cs.energy.asset.api.enums.WithdrawStatusEnum;
import com.cs.energy.asset.api.event.WithdrawEvent;
import com.cs.energy.asset.api.service.AssetService;
import com.cs.energy.asset.api.service.WithdrawFlowService;
import com.cs.energy.asset.server.mapper.AssetFlowMapper;
import com.cs.energy.asset.server.mapper.WithdrawFlowMapper;
import com.cs.energy.evm.api.entity.ChainWork;
import com.cs.energy.evm.api.entity.Symbol;
import com.cs.energy.evm.api.enums.ChainWorkProcessedEnum;
import com.cs.energy.evm.api.enums.ChainWorkTxStatusEnum;
import com.cs.energy.evm.api.enums.ChainWorkTypeEnum;
import com.cs.energy.evm.api.enums.SymbolTypeEnum;
import com.cs.energy.evm.api.service.SymbolService;
import com.cs.energy.evm.server.mapper.ChainWorkMapper;
import com.cs.energy.global.constants.CacheKey;
import com.cs.energy.global.constants.Gkey;
import com.cs.energy.member.api.entity.Member;
import com.cs.energy.member.api.enums.ChainEnum;
import com.cs.energy.member.server.mapper.MemberMapper;
import com.cs.energy.system.api.dto.GlobalConfigDTO;
import com.cs.energy.system.api.service.ConfigService;
import com.cs.energy.system.server.config.prop.AppProperties;
import com.cs.energy.thd.api.enums.MerchantPaymentTypeEnum;
import com.cs.sp.enums.YesNoByteEnum;
import com.cs.sp.enums.YesNoIntEnum;
import com.cs.sp.util.DateUtil;
import com.cs.sp.util.StringUtil;
import com.cs.web.spring.helper.CacheClient;
import com.cs.web.spring.helper.aeshelper.AesHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;
import org.web3j.crypto.WalletUtils;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Map;

import static com.cs.sp.common.WebAssert.*;

/**
 * <p>
 * 提现流水 服务实现类
 * </p>
 *
 * @author gpthk
 * @since 2024-10-03
 */
@Slf4j
@Service
public class WithdrawFlowServiceImpl extends ServiceImpl<WithdrawFlowMapper, WithdrawFlow> implements WithdrawFlowService {


    @Autowired
    private AssetService assetService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private CacheClient cacheClient;

    @Autowired
    private AesHelper aesHelper;

    @Autowired
    private ChainWorkMapper chainWorkMapper;

    @Autowired
    private AssetFlowMapper assetFlowMapper;

    @Autowired
    private SymbolService symbolService;

    @Autowired
    private AppProperties appProperties;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Override
    public WithdrawFlow add(WithdrawFlow req) {
        GlobalConfigDTO globalConfig = configService.getGlobalConfig(GlobalConfigDTO.class);
        expect(YesNoIntEnum.YES.eq(globalConfig.getWithdrawFlag()), "chk.withdraw.wait");
        ChainEnum chainEnum = ChainEnum.of(req.getChain());
        isNotNull(chainEnum, "chk.common.invalid", "chain");
        String arriveSymbol = Gkey.TOKEN;
        if (ChainEnum.CNY.equals(chainEnum)) {
            isTrue(NumberUtil.isNumber(req.getArriveAddr()), "chk.common.invalid", "arriveAddr");
        } else {
            expect(WalletUtils.isValidAddress(req.getArriveAddr()), "chk.withdraw.addInvalid");
        }
        String keys = StrUtil.format("{}:{}", req.getChain(), req.getSymbol());
        return cacheClient.withLock(CacheKey.USER_WITHDRAW + keys + ":" + req.getUid(), () -> {
            Map<String, Symbol> chainSymbolMap = symbolService.listAsTypeChainSymolMap();
            Symbol currency = chainSymbolMap.get(StrUtil.join(":", SymbolTypeEnum.WITHDRAWAL.getCode(),
                    req.getChain(), req.getSymbol() + arriveSymbol));
            expectNotNull(currency, "chk.withdraw.cantWithdraw");
            expect(YesNoByteEnum.YES.eq(currency.getCanWithdraw()), "chk.withdraw.cantWithdraw");
            // 最小提现金额限制
            if (currency.getWithdrawMinAmount() != null && currency.getWithdrawMinAmount().compareTo(BigDecimal.ZERO) > 0) {
                expect(req.getQuantity().compareTo(currency.getWithdrawMinAmount()) >= 0, "chk.withdraw.minLimit",
                        currency.getWithdrawMinAmount().stripTrailingZeros().toPlainString());
                if (YesNoByteEnum.YES.eq(currency.getWithdrawAmountMultipled())) {
                    BigDecimal reminder = req.getQuantity().remainder(currency.getWithdrawMinAmount());
                    expect(reminder.compareTo(BigDecimal.ZERO) == 0, "chk.withdraw.divisible", currency.getWithdrawMinAmount().stripTrailingZeros().toPlainString());
                }
            } else {
                // 至少要大于0
                expect(req.getQuantity().compareTo(BigDecimal.ZERO) > 0, "chk.withdraw.minLimit", "0");
            }

            if (StringUtils.hasText(currency.getActivePeriod())) {
                Integer hm = DateUtil.getHourMinute();
                boolean ok = Arrays.stream(currency.getActivePeriod().split(","))
                        .map(period -> period.split("-"))
                        .anyMatch(times -> hm >= Integer.parseInt(times[0]) && hm <= Integer.parseInt(times[1]));
                expect(ok, "chk.withdraw.activePeriod");
            }

            long now = System.currentTimeMillis();
            Member member = memberMapper.selectById(req.getUid());
            expect(YesNoByteEnum.YES.eq(member.getStatus()), "chk.auth.suspend");
            expect(YesNoByteEnum.YES.eq(member.getCanWithdraw()), "chk.withdraw.wait");

            expect(now >= member.getCoolDownAt(), "chk.withdraw.coldDown", DateUtil.formatTime(member.getCoolDownAt()));
            // 人民币需要收款方式
            String scene = AssetSceneEnum.WITHDRAW.getCode();
            if (ChainEnum.CNY.equals(chainEnum)) {
                Byte payType = null;
                // TODO: 绑定支付宝支付出金
                MerchantPaymentTypeEnum typeEnum = MerchantPaymentTypeEnum.of(payType);
                switch (typeEnum) {
                    case ALIPAY:
                        scene = AssetSceneEnum.WITHDRAW_ALIPAY.getCode();
                        break;
                    case UNIONPAY:
                        scene = AssetSceneEnum.WITHDRAW_UNIONPAY.getCode();
                        break;
                    case WX:
                        scene = AssetSceneEnum.WITHDRAW_WECHAT.getCode();
                        break;
                    case UNION:
                        scene = AssetSceneEnum.WITHDRAW_UNION.getCode();
                        break;
                    default:
                        break;
                }
            }

            Asset asset = assetService.getOne(new QueryWrapper<Asset>().lambda()
                    .eq(Asset::getType, AssetTypeEnum.DEFAULT.getCode())
                    .eq(Asset::getUid, member.getId())
                    .eq(Asset::getSymbol, req.getSymbol())
            );
            expectNotNull(asset, "chk.withdraw.insufficientBalance");

            req.setQuantity(req.getQuantity().setScale(currency.getQuoteDecimals(), RoundingMode.FLOOR));
            if (req.getQuantity().compareTo(asset.getBalance().subtract(asset.getLocked())) > 0) {
                if (asset.getLocked().compareTo(BigDecimal.ZERO) > 0) {
                    throwBizException("chk.withdraw.insufficientBalanceWithLock", asset.getLocked().stripTrailingZeros().toPlainString());
                } else {
                    throwBizException("chk.withdraw.insufficientBalance");
                }
            }

            req.setFee(req.getQuantity().multiply(currency.getFeeRate()));
            req.setExchangeRate(currency.getExchangeRate());
            req.setArriveValue(req.getQuantity().subtract(req.getFee()));
            // 取整个
            req.setArriveQuantity(req.getArriveValue().multiply(currency.getExchangeRate()).setScale(currency.getQuoteDecimals(), RoundingMode.FLOOR));
            req.setArriveSymbol(currency.getQuoteCoin());
            req.setCreateAt(System.currentTimeMillis());
            req.setAuditStatus(YesNoByteEnum.NO.getCode());
            req.setStatus(WithdrawStatusEnum.COMMIT.getCode());
            req.setYmd(DateUtil.getYmd(req.getCreateAt()));
            getBaseMapper().insert(req);

            AssetFlow assetflow = new AssetFlow();
            assetflow.setType(AssetTypeEnum.DEFAULT.getCode());
            assetflow.setUid(member.getId());
            assetflow.setSymbol(req.getSymbol());
            assetflow.setBalance(req.getQuantity().negate());
            assetflow.setScene(scene);
            if (!ChainEnum.CNY.equals(chainEnum)) {
                assetflow.setMemo(StringUtil.senseWallet(req.getArriveAddr()));
            }
            assetflow.setRelateId(req.getId());
            assetService.updateAsset(assetflow, asset);

            SpringUtil.publishEvent(new WithdrawEvent(this, req, null));
            return req;
        });
    }


    @Override
    public void updateWithdrawStatus(ChainWork chainWork) {
        ChainWork update = new ChainWork();
        update.setId(chainWork.getId());
        WithdrawFlow withdrawFlow = getBaseMapper().selectById(chainWork.getRelateId());
        if (withdrawFlow == null) {
            update.setProcessed(ChainWorkProcessedEnum.FAIL.getCode());
            update.setProcessMsg("Withdraw not found");
            log.warn("Withdraw-confirm {}:{}, {}", chainWork.getId(), "notFound", chainWork.getHash());
        } else {
            if (YesNoByteEnum.YES.eq(chainWork.getReceiptStatus())) {
                WithdrawFlow confirmed = new WithdrawFlow();
                confirmed.setConfirmAt(chainWork.getConfirmAt());
                confirmed.setStatus(WithdrawStatusEnum.DONE.getCode());
                confirmed.setClaimAt(chainWork.getBlockTime());
                confirmed.setClaimTx(chainWork.getHash());
                confirmed.setTxNum(1);
                int row = getBaseMapper().update(confirmed, Wrappers.lambdaUpdate(WithdrawFlow.class)
                        .eq(WithdrawFlow::getId, chainWork.getRelateId())
                        .eq(WithdrawFlow::getStatus, WithdrawStatusEnum.CONFIRMING.getCode())
                );
                if (row < 1) {
                    log.warn("Withdraw-confirm status update failed: {}, status: {}", chainWork.getRelateId(), withdrawFlow.getStatus());
                }
                update.setProcessed(ChainWorkProcessedEnum.OK.getCode());
            } else {
                // 如果确认是失败的，则重新修改状态，重新发起
                WithdrawFlow confirmed = new WithdrawFlow();
                confirmed.setId(withdrawFlow.getId());
                confirmed.setStatus(WithdrawStatusEnum.AUDITED.getCode());
                int row = getBaseMapper().update(confirmed, Wrappers.lambdaUpdate(WithdrawFlow.class)
                        .eq(WithdrawFlow::getId, withdrawFlow.getId())
                        .eq(WithdrawFlow::getStatus, WithdrawStatusEnum.CONFIRMING.getCode())
                );
                if (row < 1) {
                    log.info("Withdraw-confirm fail: {}, {}:{}", chainWork.getRelateId(), chainWork.getReceiptStatus(), chainWork.getHash());
                }
            }
            log.info("Withdraw-confirm {}:{}, U: {}, {} {}", chainWork.getRelateId(), chainWork.getReceiptStatus(),
                    withdrawFlow.getUid(), withdrawFlow.getArriveQuantity().stripTrailingZeros().toPlainString(), withdrawFlow.getArriveSymbol());
        }
        chainWorkMapper.updateById(update);

    }


    @PostConstruct
    public void init() {

    }

    @Override
    public void checkAndWithdraw(WithdrawFlow req) {

        GlobalConfigDTO globalConfig = configService.getGlobalConfig(GlobalConfigDTO.class);
        if(req.getQuantity().compareTo(globalConfig.getWithdrawAuditAmount()) >= 0) {
            return;
        }

        doWithdraw(req);
    }

    private void doWithdraw(WithdrawFlow req) {
        // TODO: 提现风控及检查
        Map<String, Symbol> chainSymbolMap = symbolService.listAsTypeChainSymolMap();

        Symbol currency = chainSymbolMap.get(StrUtil.join(":", SymbolTypeEnum.WITHDRAWAL.getCode(),
                req.getChain(), req.getSymbol() + req.getArriveSymbol()));

        if (currency == null) {
            log.warn("Withdraw-withdraw symbol {} not found", req.getSymbol());
            return;
        }

        JSONObject param = new JSONObject();
        param.put(Gkey.UID, req.getUid());
        ChainWork chainWork = new ChainWork();
        chainWork.setParam(param.toJSONString());
        chainWork.setChain(ChainEnum.BSC.getCode());
        chainWork.setType(ChainWorkTypeEnum.WITHDRAW.getCode());
        // 不记录发出地址，执行的时候再记录
        chainWork.setToAddr(req.getArriveAddr());
        chainWork.setContract(currency.getQuoteCa());
        chainWork.setSymbol(currency.getQuoteCoin());
        chainWork.setAmount(req.getArriveQuantity());
        chainWork.setStatus(YesNoByteEnum.YES.getCode());
        chainWork.setTxStatus(ChainWorkTxStatusEnum.WAIT.getCode());
        chainWork.setCreateAt(System.currentTimeMillis());
        // 添加排队时间
        // 1、本身会会添加到执行队列的
        // 2、同时又防止被定时任务立即捞起来，重复加队列
        chainWork.setQueueAt(chainWork.getCreateAt() + Gkey.EVM_TX_WAIT);
        chainWork.setRelateId(req.getId());

        transactionTemplate.execute(tx->{
            WithdrawFlow flow = new WithdrawFlow();
            flow.setStatus(WithdrawStatusEnum.AUDITED.getCode());
            if(YesNoByteEnum.NO.eq(req.getAuditStatus())){
                flow.setAuditStatus(YesNoByteEnum.YES.getCode());
                flow.setAuditAt(System.currentTimeMillis());
            }
            int row = getBaseMapper().update(flow, Wrappers.lambdaUpdate(WithdrawFlow.class)
                    .eq(WithdrawFlow::getId, req.getId())
                    .eq(WithdrawFlow::getStatus, WithdrawStatusEnum.COMMIT.getCode())
            );

            if(row <= 0){
                // 回滚插入事件
                return null;
            }

            chainWorkMapper.insert(chainWork);

            AssetFlow assetFlow = assetFlowMapper.selectOne(Wrappers.lambdaQuery(AssetFlow.class)
                    .eq(AssetFlow::getRelateId, req.getId())
                    .eq(AssetFlow::getScene, AssetSceneEnum.WITHDRAW.getCode())
            );

            if (assetFlow != null) {
                AssetFlow updated = new AssetFlow();
                updated.setId(assetFlow.getId());
                updated.setChainWorkId(chainWork.getId());
                assetFlowMapper.updateById(updated);
            }

//            SpringUtil.getBean(WithdrawWorkQueueService.class).add(chainWork);
            return null;
        });

    }


    @Override
    public void checkCnyAndWithdraw(WithdrawFlow req) {
        Map<String, Symbol> chainSymbolMap = symbolService.listAsTypeChainSymolMap();

        Symbol currency = chainSymbolMap.get(StrUtil.join(":", SymbolTypeEnum.WITHDRAWAL.getCode(),
                req.getChain(), req.getSymbol() + req.getArriveSymbol()));

        if (currency == null) {
            log.warn("Withdraw-withdraw symbol {} not found", req.getSymbol());
            return;
        }
        WithdrawFlow update = new WithdrawFlow();
        update.setId(req.getId());
        update.setAuditStatus(YesNoByteEnum.YES.getCode());
        int row = getBaseMapper().update(update, Wrappers.lambdaUpdate(WithdrawFlow.class)
                .eq(WithdrawFlow::getId, req.getId())
                .eq(WithdrawFlow::getAuditStatus, YesNoByteEnum.NO.getCode())
        );
        log.info("Withdraw-cny-audited {} {}", req.getId(), row);

    }
}
