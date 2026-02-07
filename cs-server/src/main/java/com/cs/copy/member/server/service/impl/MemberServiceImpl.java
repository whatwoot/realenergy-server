package com.cs.copy.member.server.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cs.copy.asset.api.entity.AssetFlow;
import com.cs.copy.asset.api.service.AssetService;
import com.cs.copy.asset.api.vo.AssetFlowListVO;
import com.cs.copy.asset.server.mapper.AssetFlowMapper;
import com.cs.copy.asset.server.mapper.AssetMapper;
import com.cs.copy.chain.api.entity.ChainAddress;
import com.cs.copy.chain.api.service.ChainAddressService;
import com.cs.copy.global.constants.CacheKey;
import com.cs.copy.global.constants.Gkey;
import com.cs.copy.member.api.entity.InviteLevel;
import com.cs.copy.member.api.entity.Login;
import com.cs.copy.member.api.entity.Member;
import com.cs.copy.member.api.entity.MemberWallet;
import com.cs.copy.member.api.enums.LoginTypeEnum;
import com.cs.copy.member.api.enums.MemberWalletTypeEnum;
import com.cs.copy.member.api.event.BindEvent;
import com.cs.copy.member.api.event.InviteLvEvent;
import com.cs.copy.member.api.event.LevelChgEvent;
import com.cs.copy.member.api.service.InviteLevelService;
import com.cs.copy.member.api.service.MemberService;
import com.cs.copy.member.api.vo.InviteSummary;
import com.cs.copy.member.api.vo.LevelsNumVO;
import com.cs.copy.member.server.mapper.LoginMapper;
import com.cs.copy.member.server.mapper.MemberMapper;
import com.cs.copy.member.server.mapper.MemberWalletMapper;
import com.cs.copy.system.api.dto.GlobalConfigDTO;
import com.cs.copy.system.api.enums.SseNameEnum;
import com.cs.copy.system.api.event.ReBuildCacheEvent;
import com.cs.copy.system.api.service.ConfigService;
import com.cs.copy.system.api.service.SseService;
import com.cs.copy.system.api.vo.SmallTeamPerformanceVO;
import com.cs.copy.system.server.config.prop.AppProperties;
import com.cs.sp.common.BeanCopior;
import com.cs.sp.enums.YesNoByteEnum;
import com.cs.sp.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.web3j.crypto.WalletUtils;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.cs.sp.common.WebAssert.*;

/**
 * <p>
 * 用户-成员表 服务实现类
 * </p>
 *
 * @author gpthk
 * @since 2024-09-29
 */
@Slf4j
@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements MemberService {

    @Autowired
    private SseService sseService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private AssetService assetService;

    @Autowired
    private ChainAddressService chainAddressService;
    @Autowired
    private InviteLevelService inviteLevelService;
    @Autowired
    private MemberWalletMapper memberWalletMapper;
    @Autowired
    private LoginMapper loginMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private AppProperties appProperties;
    @Autowired
    private AssetMapper assetMapper;
    private AssetFlowMapper assetFlowMapper;
    @Autowired
    private MemberMapper memberMapper;

    @EventListener
    @Async
    public void onChg(LevelChgEvent event) {
    }

    @EventListener
    @Async
    public void onStart(ReBuildCacheEvent event) {
        listLevelsNum(true);
    }

    @Override
    public int updateBindInvite(Member member) {
        int row = getBaseMapper().updateNoLoop(member);
        if (row > 0) {
            SpringUtil.publishEvent(new BindEvent(this, member));
        }
        return row;
    }


    @Override
    public int addBindInvite(Member member) {
        // 所有父级邀请+1
        Member update = new Member();
        update.setInviteNum(1);
        update.setId(member.getId());
        getBaseMapper().updateParentChange(update);

        Member updateDirect = new Member();
        updateDirect.setId(member.getPid());
        updateDirect.setDirectInviteNum(1);
        getBaseMapper().updateChange(updateDirect);
        return 0;
    }

    /**
     * 10级以上有直推奖励
     *
     * @param assetFlow
     */
    @Override
    public void addDirectPrize(AssetFlow assetFlow) {
        Member member = getById(assetFlow.getUid());
        if (member.getPid() == null) {
            return;
        }
        Member parent = getById(member.getPid());
        if (parent.getLevelId() < Gkey.DIRECT_PRIZE_LEVEL) {
            return;
        }
        GlobalConfigDTO config = configService.getGlobalConfig(GlobalConfigDTO.class);
        if (config.getDirectInviteRate() == null || config.getDirectInviteRate().compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        AssetFlow newPrize = new AssetFlow();
        newPrize.setSymbol(assetFlow.getSymbol());
        // TODO: scene
        newPrize.setUid(parent.getId());
        newPrize.setYmd(assetFlow.getYmd());
        newPrize.setBalance(BigDecimal.ZERO);
        newPrize.setFrozen(assetFlow.getBalance().multiply(config.getDirectInviteRate()));
        assetService.updateAsset(newPrize);
        sseService.sendTo(parent.getId(), SseNameEnum.EXCHANGE_DIRECT_PRIZE.getCode(), BeanCopior.map(newPrize, AssetFlowListVO.class));
    }

    @Override
    public int resetRechargeAddr(Boolean force, Long uid, String symbol, String chain) {
        List<Member> list;
        if (uid != null) {
            Member member = getById(uid);
            expectNotNull(member, "chk.common.invalid", "uid");
            list = new ArrayList<>();
            list.add(member);
        } else {
            list = list();
        }
        MemberWallet add;
        MemberWallet memberWallet;
        ChainAddress chainAddress;
        Integer num = 0;
        for (Member member : list) {
            memberWallet = memberWalletMapper.selectOne(new QueryWrapper<MemberWallet>().lambda()
                    .eq(MemberWallet::getChain, chain)
                    .eq(MemberWallet::getUid, member.getId())
                    .orderByDesc(MemberWallet::getWeight)
                    .last("limit 1")
            );
            if (force || memberWallet == null) {
                chainAddress = chainAddressService.genWallet();
                add = new MemberWallet();
                add.setType(MemberWalletTypeEnum.RECHARGE.getCode());
                add.setUid(member.getId());
                add.setChain(chain);
                add.setWallet(chainAddress.getAddr());
                if (memberWallet != null) {
                    add.setWeight(memberWallet.getWeight() + 1);
                    add.setSeq(memberWallet.getSeq() + 1);
                }
                num += memberWalletMapper.insert(add);
            }
        }

        return num;
    }

    @Override
    public void updateInvalid(Long uid) {
        Member member = getBaseMapper().selectById(uid);
        if (member.getValid() != null) {
            return;
        }
        Member update = new Member();
        update.setId(member.getId());
        update.setValid(YesNoByteEnum.YES.getCode());
        update.setValidAt(System.currentTimeMillis());
        int row = getBaseMapper().update(update, Wrappers.lambdaUpdate(Member.class)
                .eq(Member::getId, uid)
                .isNull(Member::getValidAt)
        );
        // 变成有效用户时
        // 1、计算自己门下有效的数量
        // 2、给上级增加有效的直推数量
        if (row > 0) {
            Long num = getBaseMapper().selectCount(new QueryWrapper<Member>().lambda()
                    .eq(Member::getPid, member.getId())
                    .eq(Member::getValid, YesNoByteEnum.YES.getCode())
            );

            List<InviteLevel> list = inviteLevelService.listAll();
            Integer lv = 0;
            for (InviteLevel inviteLevel : list) {
                if (num >= inviteLevel.getInviteNum()) {
                    lv = inviteLevel.getId();
                }
            }
            // 更新自己的级别
            Member updateSelf = new Member();
            updateSelf.setId(member.getId());
            updateSelf.setInviteLevelId(lv);
            getBaseMapper().updateById(updateSelf);
            // 2、给上级
            if (member.getPid() == null) {
                return;
            }
            Member parent = getBaseMapper().selectById(member.getPid());
            if (parent == null) {
                log.info("INVALID-parent missing {}", member.getPid());
                return;
            }
            // 有下一级别，则表示可以升级
            Map<Integer, InviteLevel> inviteLvMap = list.stream().collect(Collectors.toMap(InviteLevel::getId, item -> item));
            InviteLevel nextLv = inviteLvMap.get(parent.getInviteLevelId() + 1);
            if (nextLv == null) {
                return;
            }
            InviteLevel max = list.get(list.size() - 1);
            int ok = getBaseMapper().updateInviteLvChange(parent.getId(), 1, max.getId());
            if (ok > 0) {
                // 邀请等级变更事件
                SpringUtil.publishEvent(new InviteLvEvent(this, parent, nextLv));
            }
        }
    }

    @Override
    public InviteSummary groupInviteSummary(Long id) {
        return getBaseMapper().groupInviteSummary(id);
    }

    @Override
    public void updateValid(Long uid) {
        Member member = getBaseMapper().selectById(uid);
        if (member == null || YesNoByteEnum.YES.eq(member.getValid())) {
            return;
        }
        Member valid = new Member();
        valid.setValid(YesNoByteEnum.YES.getCode());
        valid.setValidAt(System.currentTimeMillis());
        int row = getBaseMapper().update(valid, Wrappers.lambdaUpdate(Member.class)
                .eq(Member::getId, uid)
                .eq(Member::getValid, YesNoByteEnum.NO.getCode())
        );
        log.info("Invest-first {},{}", member.getId(), row);
    }

    @Override
    public int updateEndWizard(Long uid) {
        Member member = getBaseMapper().selectById(uid);
        if (member == null || YesNoByteEnum.YES.eq(member.getWizardEnd())) {
            return 0;
        }
        Member valid = new Member();
        valid.setWizardEnd(YesNoByteEnum.YES.getCode());
        valid.setWizardEndAt(System.currentTimeMillis());
        int row = getBaseMapper().update(valid, Wrappers.lambdaUpdate(Member.class)
                .eq(Member::getId, uid)
                .eq(Member::getWizardEnd, YesNoByteEnum.NO.getCode())
        );
        log.info("endWizard {},{}", member.getId(), row);
        return row;
    }

    @Override
    public SmallTeamPerformanceVO getSmallTeamPerformance(Login login) {
        Login bscAccount = null;
        // 绑定时的账户就是钱包账户
        if (login.getId() != null) {
            bscAccount = login;
        } else if (login.getUid() != null) {
            bscAccount = loginMapper.selectOne(new QueryWrapper<Login>().lambda()
                    .eq(Login::getUid, login.getUid())
                    .eq(Login::getType, LoginTypeEnum.BSC.getCode())
            );
        } else if (LoginTypeEnum.BSC.eq(login.getType())) {
            bscAccount = loginMapper.selectOne(new QueryWrapper<Login>().lambda()
                    .eq(Login::getType, login.getType())
                    .eq(Login::getAccount, login.getAccount().toLowerCase())
            );
        }
        if (bscAccount == null) {
            return null;
        }
        Long uid = bscAccount.getUid();

        List<Member> members = getBaseMapper().selectList(new QueryWrapper<Member>().lambda()
                .eq(Member::getPid, uid));
        SmallTeamPerformanceVO vo = new SmallTeamPerformanceVO();
        vo.setAddr(bscAccount.getAccount());
        vo.setUid(bscAccount.getUid());
        vo.setCreateAt(bscAccount.getBindAt());

        if (members.isEmpty()) {
            vo.setAmount(BigDecimal.ZERO);
            return vo;
        }

        BigDecimal biggest = BigDecimal.ZERO;
        BigDecimal sum = BigDecimal.ZERO;
        BigDecimal temp;
        for (Member member : members) {
            temp = member.getPerformance().add(member.getTeamPerformance());
            if (temp.compareTo(biggest) > 0) {
                biggest = temp;
            }
            sum = sum.add(temp);
        }
        vo.setAmount(sum.subtract(biggest));
        return vo;
    }

    @Override
    public Map<String, String> listLevelsNum() {
        return listLevelsNum(false);
    }

    @Override
    public Map<String, String> listLevelsNum(boolean force) {
        BoundHashOperations<String, String, String> ops = null;
        if (!force) {
            ops = stringRedisTemplate.boundHashOps(CacheKey.LEVEL_NUM_KEY);
            Map<String, String> map = ops.entries();
            if (map != null && !map.isEmpty()) {
                return map;
            }
        }
        if (ops == null) {
            ops = stringRedisTemplate.boundHashOps(CacheKey.LEVEL_NUM_KEY);
        }
        List<LevelsNumVO> levelsNumVOS = getBaseMapper().groupLevelsNum();
        Map<String, String> cache = levelsNumVOS.stream().collect(Collectors.toMap(i -> String.valueOf(i.getLevelId()),
                i -> String.valueOf(i.getNum())));
        ops.putAll(cache);
        return cache;
    }

    @PostConstruct
    public void initSys() {
    }


    /**
     * 只执行一次
     */
    @PostConstruct
    public void init() {

    }


    @Override
    public Member updateChangeWallet(String old, String newer) {
        Login login = loginMapper.selectOne(Wrappers.lambdaQuery(Login.class)
                .eq(Login::getType, LoginTypeEnum.BSC.getCode())
                .eq(Login::getAccount, old.toLowerCase())
        );
        isNotNull(login, "chk.common.invalid", "old");
        isTrue(WalletUtils.isValidAddress(newer), "chk.common.invalid", "newer");
        Login newLogin = loginMapper.selectOne(Wrappers.lambdaQuery(Login.class)
                .eq(Login::getType, LoginTypeEnum.BSC.getCode())
                .eq(Login::getAccount, newer.toLowerCase())
        );
        isNull(newLogin, "chk.common.invalid", "newer");

        Member member = new Member();
        member.setId(login.getUid());
        member.setMainAccount(newer.toLowerCase());
        member.setNickname(StringUtil.senseWallet(newer));
        memberMapper.updateById(member);

        Login chg = new Login();
        chg.setId(login.getId());
        chg.setAccount(newer.toLowerCase());
        loginMapper.updateById(chg);
        return member;
    }

}
