package com.cs.energy.member.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cs.energy.member.api.entity.Login;
import com.cs.energy.member.api.entity.Member;
import org.apache.commons.lang3.tuple.Pair;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author gpthk
 * @since 2025-02-20
 */
public interface LoginService extends IService<Login> {

    void addCoolDown(Login login);

    Pair<Member, Login> addLoginByWallet(String wallet);
}
