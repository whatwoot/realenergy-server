package com.cs.copy.member.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cs.copy.asset.api.dto.MemberPerformanceDTO;
import com.cs.copy.member.api.dto.MemberWithLevelDTO;
import com.cs.copy.member.api.entity.Member;
import com.cs.copy.member.api.vo.InviteSummary;
import com.cs.copy.member.api.vo.LevelsNumVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 用户-成员表 Mapper 接口
 * </p>
 *
 * @author gpthk
 * @since 2024-09-29
 */
@Mapper
public interface MemberMapper extends BaseMapper<Member> {


    int updateNoLoop(Member member);

    List<MemberWithLevelDTO> listParents(Long id);
    List<MemberWithLevelDTO> listChildren(Long id);

    int updateBonusChange(Member member);

    int updateChange(Member member);
    int updateChangeChecked(Member member);
    int updateToValid(Member member);

    int updateParentChange(Member member);
    int updateParentPerformanceChange(Member member);

    int updateInviteLvChange(@Param("uid") Long uid,@Param("change") Integer change,@Param("max") Integer max);

    InviteSummary groupInviteSummary(Long id);

    List<LevelsNumVO> groupLevelsNum();

    List<MemberPerformanceDTO> listGenesisSmallPerformance(Set<Long> ids);

    int updatePerformanceByIds(@Param("e") Member performance,@Param("uids") Collection<Long> uids);
}
