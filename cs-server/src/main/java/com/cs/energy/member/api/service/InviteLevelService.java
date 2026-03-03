package com.cs.energy.member.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cs.energy.member.api.entity.InviteLevel;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author gpthk
 * @since 2024-12-11
 */
public interface InviteLevelService extends IService<InviteLevel> {

    List<InviteLevel> listAll();
    List<InviteLevel> listAll(boolean force);
    Map<Integer,InviteLevel> listAsMap();
}
