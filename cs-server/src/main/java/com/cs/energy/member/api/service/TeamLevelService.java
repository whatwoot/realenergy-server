package com.cs.energy.member.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cs.energy.member.api.entity.TeamLevel;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author gpthk
 * @since 2024-12-11
 */
public interface TeamLevelService extends IService<TeamLevel> {

    List<TeamLevel> listAll();

    List<TeamLevel> listAll(boolean force);

    Map<Integer, TeamLevel> listAsMap();
}
