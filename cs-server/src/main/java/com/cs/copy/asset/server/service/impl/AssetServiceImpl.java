package com.cs.copy.asset.server.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cs.copy.asset.api.entity.Asset;
import com.cs.copy.asset.api.entity.AssetFlow;
import com.cs.copy.asset.api.enums.AssetSceneEnum;
import com.cs.copy.asset.api.enums.AssetTypeEnum;
import com.cs.copy.asset.api.event.AssetRefundEvent;
import com.cs.copy.asset.api.event.DepositEvent;
import com.cs.copy.asset.api.event.TransferEvent;
import com.cs.copy.asset.api.request.ExchangeRequest;
import com.cs.copy.asset.api.service.AssetService;
import com.cs.copy.asset.server.mapper.AssetFlowMapper;
import com.cs.copy.asset.server.mapper.AssetMapper;
import com.cs.copy.evm.api.entity.ChainWork;
import com.cs.copy.evm.api.enums.ChainWorkProcessedEnum;
import com.cs.copy.evm.server.mapper.ChainWorkMapper;
import com.cs.copy.global.constants.CacheKey;
import com.cs.copy.global.constants.DKey;
import com.cs.copy.global.constants.Gkey;
import com.cs.copy.member.api.entity.Member;
import com.cs.copy.member.api.entity.MemberWallet;
import com.cs.copy.member.api.enums.MemberWalletTypeEnum;
import com.cs.copy.member.server.mapper.MemberMapper;
import com.cs.copy.member.server.mapper.MemberWalletMapper;
import com.cs.copy.system.api.dto.GlobalConfigDTO;
import com.cs.copy.system.api.service.ConfigService;
import com.cs.copy.system.server.config.prop.AppProperties;
import com.cs.sp.constant.Constant;
import com.cs.sp.enums.YesNoByteEnum;
import com.cs.sp.util.DateUtil;
import com.cs.sp.util.StringUtil;
import com.cs.web.spring.helper.RedissonLockHelper;
import com.cs.web.util.BeanCopior;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.cs.sp.common.WebAssert.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author gpthk
 * @since 2024-09-29
 */
@Slf4j
@Service
public class AssetServiceImpl extends ServiceImpl<AssetMapper, Asset> implements AssetService {

    @Autowired
    private AssetFlowMapper assetFlowMapper;

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private ChainWorkMapper chainWorkMapper;

    @Autowired
    private MemberWalletMapper memberWalletMapper;

    @Autowired
    private ConfigService configService;

    @Autowired
    private AppProperties appProperties;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonLockHelper redissonLockHelper;

    @Autowired
    private AssetMapper assetMapper;


    @PostConstruct
    public void init() {
        BoundHashOperations<String, String, String> check = stringRedisTemplate.boundHashOps(CacheKey.PRICE_MAP);
        if (!Boolean.TRUE.equals(check.hasKey(Gkey.USDT))) {
            check.put(Gkey.USDT, Constant.ONE_STR);
        }
    }

    @Override
    public AssetFlow updateAsset(AssetFlow assetFlow) {
        return updateAsset(assetFlow, null);
    }

    @Override
    public AssetFlow updateAsset(AssetFlow assetFlow, Asset asset) {
        Asset exists;
        if (asset == null) {
            expectNotNull(assetFlow.getType(), "chk.asset.typeRequired");
            expectNotNull(assetFlow.getUid(), "chk.asset.uidRequired");
            expectNotNull(assetFlow.getSymbol(), "chk.asset.symbolRequired");
            exists = getBaseMapper().selectOne(new QueryWrapper<Asset>().lambda()
                    .eq(Asset::getType, assetFlow.getType())
                    .eq(Asset::getUid, assetFlow.getUid())
                    .eq(Asset::getSymbol, assetFlow.getSymbol())
            );
        } else {
            exists = getBaseMapper().selectById(asset.getId());
        }
        AssetSceneEnum sceneEnum = AssetSceneEnum.of(assetFlow.getScene());
        isNotNull(sceneEnum, "chk.asset.sceneInvalid");
        BigDecimal balance = assetFlow.getBalance() != null ? assetFlow.getBalance() : BigDecimal.ZERO;
        BigDecimal frozen = assetFlow.getFrozen() != null ? assetFlow.getFrozen() : BigDecimal.ZERO;
        BigDecimal locked = assetFlow.getLocked() != null ? assetFlow.getLocked() : BigDecimal.ZERO;

        if (assetFlow.getCreateAt() == null) {
            assetFlow.setCreateAt(System.currentTimeMillis());
        }
        if (assetFlow.getYmd() == null) {
            assetFlow.setYmd(DateUtil.getYmd(assetFlow.getCreateAt()));
        }

        assetFlow.setBeginBalance(exists == null ? BigDecimal.ZERO : exists.getBalance());
        assetFlow.setBeginFrozen(exists == null ? BigDecimal.ZERO : exists.getFrozen());

        int rows;
        if (exists == null) {
            // 不能有任何一方
            expect(validOfAll(balance, frozen, locked), "chk.asset.notEnough");
            // 事务里，插入不会引入锁，更新才会引入，所以这里先插入
            assetFlowMapper.insert(assetFlow);
            exists = new Asset();
            exists.setType(assetFlow.getType());
            exists.setUid(assetFlow.getUid());
            exists.setSymbol(assetFlow.getSymbol());
            exists.setLocked(locked);
            exists.setBalance(balance);
            exists.setFrozen(frozen);
            rows = getBaseMapper().insertOrUpdate(exists);
            expectGt0(rows, "chk.asset.exists");
        } else {
            // 事务里，插入不会引入锁，更新才会引入，所以这里先插入
            assetFlowMapper.insert(assetFlow);
            // 再更新
            Asset updateAsset = new Asset();
            updateAsset.setId(exists.getId());
            updateAsset.setLocked(locked);
            updateAsset.setBalance(balance);
            updateAsset.setFrozen(frozen);
            // 扣赢利允许扣成负数
            if (AssetSceneEnum.COPYER_PROFIT.eq(assetFlow.getScene())) {
                rows = getBaseMapper().updateChangeCanBelowZero(updateAsset);
            } else {
                rows = getBaseMapper().updateChange(updateAsset);
            }
            expectGt0(rows, "chk.asset.notEnough");
        }

        return assetFlow;
    }

    /**
     * 不能全为空
     * 至少有一个大于0的数
     */
    private static boolean validOfAll(BigDecimal... balances) {
        int count = 0;
        int negativeCount = 0;
        for (BigDecimal bal : balances) {
            if (bal != null) {
                count++;
                if (bal.compareTo(BigDecimal.ZERO) < 0) {
                    negativeCount++;
                }
            }
        }
        // 不能全为负
        if (negativeCount < count) {
            return true;
        }
        return false;
    }

    @Override
    public AssetFlow updateRefundOptional(AssetFlow assetFlow) {
        return updateRefund(assetFlow, false);
    }

    @Override
    public AssetFlow updateRefund(AssetFlow assetFlow) {
        return updateRefund(assetFlow, true);
    }

    private AssetFlow updateRefund(AssetFlow assetFlow, boolean force) {
        AssetFlow refundFlow;
        if (assetFlow.getId() != null) {
            refundFlow = assetFlowMapper.selectById(assetFlow.getId());
        } else {
            LambdaQueryWrapper<AssetFlow> lambda = new QueryWrapper<AssetFlow>().lambda();
            if (assetFlow.getType() != null) {
                lambda.eq(AssetFlow::getType, assetFlow.getType());
            }
            if (assetFlow.getUid() != null) {
                lambda.eq(AssetFlow::getUid, assetFlow.getUid());
            }
            if (assetFlow.getSymbol() != null) {
                lambda.eq(AssetFlow::getSymbol, assetFlow.getSymbol());
            }
            if (assetFlow.getScene() != null) {
                lambda.eq(AssetFlow::getScene, assetFlow.getScene());
            }
            if (assetFlow.getRelateId() != null) {
                lambda.eq(AssetFlow::getRelateId, assetFlow.getRelateId());
            }
            refundFlow = assetFlowMapper.selectOne(lambda);
        }
        if (force) {
            expectNotNull(refundFlow, "chk.asset.notExists");
        } else {
            if (refundFlow == null) {
                log.warn("Refund-flow not found {}, relateId {}", assetFlow.getScene(), assetFlow.getRelateId());
                return assetFlow;
            }
        }
        AssetFlow refund = new AssetFlow();
        refund.setRefunded(YesNoByteEnum.YES.getCode());
        refund.setRefundAt(System.currentTimeMillis());
        int refuned = assetFlowMapper.update(refund, Wrappers.lambdaUpdate(AssetFlow.class)
                .eq(AssetFlow::getId, refundFlow.getId())
                .in(AssetFlow::getScene, AssetSceneEnum.WITHDRAW.getCode(), AssetSceneEnum.WITHDRAW_WECHAT.getCode(),
                        AssetSceneEnum.WITHDRAW_ALIPAY.getCode(), AssetSceneEnum.WITHDRAW_UNIONPAY.getCode()
                )
                .eq(AssetFlow::getRefunded, YesNoByteEnum.NO.getCode())
        );
        if (refuned > 0) {
            BeanCopior.copy(refund, refundFlow);
            SpringUtil.publishEvent(new AssetRefundEvent(this, refundFlow));
            // 退款时，资产账户肯定存在，因为已经有了流水
            Asset asset = getBaseMapper().selectOne(new QueryWrapper<Asset>().lambda()
                    .eq(Asset::getType, refundFlow.getType())
                    .eq(Asset::getUid, refundFlow.getUid())
                    .eq(Asset::getSymbol, refundFlow.getSymbol())
            );
            expectNotNull(asset, "chk.asset.accountNotFound");
            Asset refundAsset = new Asset();
            refundAsset.setId(asset.getId());
            if (refundFlow.getBalance().compareTo(BigDecimal.ZERO) != 0) {
                refundAsset.setBalance(refundFlow.getBalance().negate());
            }
            if (refundFlow.getFrozen().compareTo(BigDecimal.ZERO) != 0) {
                refundAsset.setFrozen(refundFlow.getFrozen().negate());
            }
            getBaseMapper().updateChange(refundAsset);
        }
        return refundFlow;
    }

    @Override
    public void addDeposit(ChainWork chainWork) {
        Long uid = null;
        // 从充值地址找用户
        MemberWallet memberWallet = memberWalletMapper.selectOne(new QueryWrapper<MemberWallet>().lambda()
                .eq(MemberWallet::getType, MemberWalletTypeEnum.RECHARGE.getCode())
                .eq(MemberWallet::getChain, chainWork.getChain())
                .eq(MemberWallet::getWallet, chainWork.getToAddr())
        );
        if (memberWallet != null) {
            uid = memberWallet.getUid();
        }
        ChainWork update = new ChainWork();
        update.setId(chainWork.getId());
        update.setProcessAt(System.currentTimeMillis());
        if (uid == null) {
            update.setProcessed(ChainWorkProcessedEnum.FAIL.getCode());
            update.setProcessMsg("Member not found");
        } else {
            // 链上记录成功的
            if (YesNoByteEnum.YES.eq(chainWork.getReceiptStatus())) {
                update.setProcessed(ChainWorkProcessedEnum.OK.getCode());
                //入账
                AssetFlow assetflow = new AssetFlow();
                assetflow.setUid(uid);
                assetflow.setScene(AssetSceneEnum.RECHARGE.getCode());
                assetflow.setSymbol(chainWork.getSymbol());
                assetflow.setBalance(chainWork.getAmount());
                assetflow.setMemo(StringUtil.senseWallet(chainWork.getFromAddr()));
                AssetFlow addFlow = updateAsset(assetflow);
                log.info("DEPOSIT-ok-{},U:{},{}", chainWork.getId(), uid, assetflow.getBalance().stripTrailingZeros().toPlainString());
                chainWork.setProcessed(update.getProcessed());
                SpringUtil.publishEvent(new DepositEvent(this, chainWork, addFlow));
            } else {
                // 事务异常
                update.setProcessed(ChainWorkProcessedEnum.FAIL.getCode());
                update.setProcessMsg("tx Fail");
            }
        }
        if (!ChainWorkProcessedEnum.OK.eq(update.getProcessed())) {
            log.info("DEPOSIT-fail-{},U:{},{}", chainWork.getId(), uid, update.getProcessMsg());
        }
        int row = chainWorkMapper.update(update, Wrappers.lambdaUpdate(ChainWork.class)
                .eq(ChainWork::getId, chainWork.getId())
                .eq(ChainWork::getProcessed, YesNoByteEnum.NO.getCode())
        );
        expectGt0(row, "chk.deposit.already");
    }

    @Override
    public void addDepositByCa(ChainWork chainWork) {
        Long uid = null;
        if (StringUtils.hasText(chainWork.getParam())) {
            Long id = JSONObject.parseObject(chainWork.getParam()).getLong("uid");
            if (id != null) {
                Member member = memberMapper.selectById(id);
                if (member != null) {
                    uid = member.getId();
                }
            }
        }
        ChainWork update = new ChainWork();
        update.setId(chainWork.getId());
        update.setProcessAt(System.currentTimeMillis());
        if (uid == null) {
            update.setProcessed(ChainWorkProcessedEnum.FAIL.getCode());
            update.setProcessMsg("Member not found");
        } else {
            // 链上记录成功的
            if (YesNoByteEnum.YES.eq(chainWork.getReceiptStatus())) {
                update.setProcessed(ChainWorkProcessedEnum.OK.getCode());
                //入账
                AssetFlow assetflow = new AssetFlow();
                assetflow.setType(AssetTypeEnum.DEFAULT.getCode());
                assetflow.setUid(uid);
                assetflow.setScene(AssetSceneEnum.RECHARGE.getCode());
                // 充值usdt转LKB
                assetflow.setSymbol(chainWork.getSymbol());
                assetflow.setBalance(chainWork.getAmount());
                assetflow.setMemo(StringUtil.senseWallet(chainWork.getFromAddr()));
                AssetFlow addFlow = updateAsset(assetflow);
                log.info("DEPOSIT-ca-ok-{},U:{},{}", chainWork.getId(), uid, assetflow.getBalance().stripTrailingZeros().toPlainString());
                chainWork.setProcessed(update.getProcessed());
                SpringUtil.publishEvent(new DepositEvent(this, chainWork, addFlow));
            } else {
                // 事务异常
                update.setProcessed(ChainWorkProcessedEnum.FAIL.getCode());
                update.setProcessMsg("tx Fail");
            }
        }
        if (!ChainWorkProcessedEnum.OK.eq(update.getProcessed())) {
            log.info("DEPOSIT-ca-fail-{},U:{},{}", chainWork.getId(), uid, update.getProcessMsg());
        }
        int row = chainWorkMapper.update(update, Wrappers.lambdaUpdate(ChainWork.class)
                .eq(ChainWork::getId, chainWork.getId())
                .eq(ChainWork::getProcessed, YesNoByteEnum.NO.getCode())
        );
        expectGt0(row, "chk.deposit.already");
    }

    @Override
    public void initPoolAsset() {
        Member member = memberMapper.selectById(Gkey.POOL_UID);
        if (member == null) {
            Member poolMember = new Member();
            poolMember.setId(Gkey.POOL_UID);
            poolMember.setNickname("公排奖池账户");
            memberMapper.insert(poolMember);
        }

        Asset asset = getBaseMapper().selectOne(new QueryWrapper<Asset>().lambda()
                .eq(Asset::getUid, Gkey.POOL_UID)
                .eq(Asset::getSymbol, Gkey.USDT)
        );
        if (asset != null) {
            DKey.POOL_ASSET_ID = asset.getId();
        } else {
            Asset pool = new Asset();
            pool.setUid(Gkey.POOL_UID);
            pool.setSymbol(Gkey.USDT);
            getBaseMapper().insert(pool);
            DKey.POOL_ASSET_ID = pool.getId();
        }

        // 矩阵奖励用户
        member = memberMapper.selectById(Gkey.POOL_PRIZE_UID);
        if (member == null) {
            Member poolMember = new Member();
            poolMember.setId(Gkey.POOL_PRIZE_UID);
            poolMember.setNickname("公排奖池回购账户");
            memberMapper.insert(poolMember);
        }

        // 提现回购账户
        member = memberMapper.selectById(Gkey.WITHDRAW_BUY_BACK_UID);
        if (member == null) {
            Member poolMember = new Member();
            poolMember.setId(Gkey.WITHDRAW_BUY_BACK_UID);
            poolMember.setNickname("公排奖池回购账户");
            memberMapper.insert(poolMember);
        }

        // 消费回购账户
        member = memberMapper.selectById(Gkey.PAY_BUY_BACK_UID);
        if (member == null) {
            Member poolMember = new Member();
            poolMember.setId(Gkey.PAY_BUY_BACK_UID);
            poolMember.setNickname("公排奖池回购账户");
            memberMapper.insert(poolMember);
        }
    }

    /**
     * 起始价： 0.00001
     * 池子U总量：0
     * 池子币总量：0
     * <p>
     * 买： 金额
     * 购买数量： 金额/价格
     * 池子币总量： 初始量 +  购买数量 * 0.7
     * 池子U总量： 原池子U总量 + 新购入金额
     * 新价格： 池子U总量 / 池子币总量
     * <p>
     * 卖：币量
     * 卖出金额：币量*最新价格
     * 池子币总量：原币量 - 卖出币量
     * 池子U总量：原池子金额 - 卖出金额 * 0.8
     * 新价格： 池子U总量 / 池子币总量
     *
     * @param req
     */
    @Override
    public void updateExchange(ExchangeRequest req) {
        Member member = memberMapper.selectById(req.getUid());
        expectNotNull(member, "auth.account.suspended");
        expect(YesNoByteEnum.YES.eq(member.getStatus()), "auth.account.suspended");
        expectNotNull(member.getPid(), "chk.member.noInvitor");
        expect(YesNoByteEnum.YES.eq(member.getValid()), "chk.exchange.investFirst");

    }


    @Override
    public int updateChange(Asset asset) {
        return getBaseMapper().updateChange(asset);
    }

    @Override
    public void updateSnapFund(Integer ymd) {
        //找到昨天快照的余额
        List<Asset> assets = getBaseMapper().selectList(Wrappers.lambdaQuery(Asset.class)
                .eq(Asset::getType, AssetTypeEnum.DEFAULT.getCode())
                .eq(Asset::getSymbol, Gkey.USDT)
                .gt(Asset::getFundBalance, 0)
        );
        log.info("Fund {}, {}", ymd, assets.size());
        if (!assets.isEmpty()) {
            GlobalConfigDTO globalConfig = configService.getGlobalConfig(GlobalConfigDTO.class);
            AssetFlow assetFlow;
            for (Asset asset : assets) {
                assetFlow = new AssetFlow();
                assetFlow.setType(asset.getType());
                assetFlow.setUid(asset.getUid());
                assetFlow.setSymbol(asset.getSymbol());
                assetFlow.setScene(AssetSceneEnum.FUND_INTEREST.getCode());
                assetFlow.setBalance(asset.getFundBalance().multiply(globalConfig.getFundRate()).setScale(4, RoundingMode.HALF_UP));
                if (assetFlow.getBalance().compareTo(BigDecimal.ZERO) > 0) {
                    updateAsset(assetFlow, asset);
                }
            }
        }
        // 开启新的快照
        int i = getBaseMapper().updateSnap(ymd);
        log.info("Fund-snap update {}, {}", ymd, i);
    }


    @Override
    public int updateGenessisWithWight(AssetFlow assetFlow) {
        BoundSetOperations<String, String> setOps = stringRedisTemplate.boundSetOps(CacheKey.GENESSIS_UIDS);
        Set<String> members = setOps.members();

        String cacheStr = stringRedisTemplate.opsForValue().get(CacheKey.SMALL_TOTAL);
        if (!StringUtils.hasText(cacheStr)) {
            return 0;
        }
        JSONObject json = JSONObject.parseObject(cacheStr);
        BigDecimal total = json.getBigDecimal("0");
        AssetFlow flow;
        BigDecimal totalPrize = assetFlow.getBalance();
        BigDecimal performance;
        BigDecimal sum = BigDecimal.ZERO;
        List<AssetFlow> batchList = new ArrayList<>();
        Set<Long> allUids = new HashSet<>();
        for (String uidStr : members) {
            performance = json.getBigDecimal(uidStr);
            // 如果他没有理财，就不会有小区，没有小区，则不需要有
            if (performance == null || performance.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            flow = BeanCopior.map(assetFlow, AssetFlow.class);
            flow.setUid(Long.parseLong(uidStr));
            // 总奖励 * 个人业绩/总业绩
            flow.setBalance(totalPrize.multiply(performance).divide(total, Gkey.TOKEN_DECIMALS, BigDecimal.ROUND_FLOOR));
            log.info("Genesis-weight {} {} {}", uidStr,
                    performance.stripTrailingZeros().toPlainString(),
                    flow.getBalance().stripTrailingZeros().toPlainString()
            );
            allUids.add(flow.getUid());
            sum = sum.add(flow.getBalance());
            batchList.add(flow);
        }
        if (batchList.isEmpty()) {
            return 0;
        }
        // 批量更新资产
        int i = assetMapper.updateBatch(assetFlow.getSymbol(), allUids, batchList);
        log.info("{} {}, {}<=>{} {}", AssetSceneEnum.of(assetFlow.getScene()).getMsg(),
                totalPrize.stripTrailingZeros().toPlainString(),
                sum.stripTrailingZeros().toPlainString(),
                batchList.size(), i);
        // 批量插入，批量更新
        if (!batchList.isEmpty()) {
            i = assetFlowMapper.insertBatch(batchList);
            log.info("insertList {}", i);
        }
        return 1;
    }

    public Asset getAssetBalance(Long uid, String symbol, byte type) {

        LambdaQueryWrapper<Asset> queryWrapper = new QueryWrapper<Asset>().lambda()
                .eq(Asset::getUid, uid)
                .eq(Asset::getSymbol, symbol)
                .eq(Asset::getType, type);
        Asset asset = this.getOne(queryWrapper);
        if (asset == null) {
            asset = new Asset();
            // 为了测试方便，如果用户没有创建过csft，则给用户初始默认创造10000csft，decimal=8
//            AssetFlow assetFlow = new AssetFlow();
//            assetFlow.setType(type);
//            assetFlow.setUid(uid);
//            assetFlow.setSymbol(symbol);
//            assetFlow.setScene(AssetSceneEnum.RECHARGE.getCode());
//            assetFlow.setBalance(BigDecimal.valueOf(10000e8));
//            this.updateAsset(assetFlow);
//            asset = this.getOne(queryWrapper);
        }
        return asset;
    }

}

