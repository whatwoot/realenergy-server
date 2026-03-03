package com.cs.energy.member.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cs.energy.asset.api.entity.AssetFlow;
import com.cs.energy.member.api.entity.Login;
import com.cs.energy.member.api.entity.Member;
import com.cs.energy.member.api.vo.InviteSummary;
import com.cs.energy.system.api.vo.SmallTeamPerformanceVO;

import java.util.Map;

/**
 * <p>
 * 用户-成员表 服务类
 * </p>
 *
 * @author gpthk
 * @since 2024-09-29
 */
public interface MemberService extends IService<Member> {

    int updateBindInvite(Member member);

    int addBindInvite(Member member);

    void addDirectPrize(AssetFlow assetFlow);

    int resetRechargeAddr(Boolean force, Long uid, String symbol, String chain);

    void updateInvalid(Long uid);

    InviteSummary groupInviteSummary(Long id);

    void updateValid(Long uid);

    int updateEndWizard(Long uid);

    SmallTeamPerformanceVO getSmallTeamPerformance(Login login);

    Map<String, String> listLevelsNum();

    Map<String, String> listLevelsNum(boolean force);

    Member updateChangeWallet(String old, String newer);
}
