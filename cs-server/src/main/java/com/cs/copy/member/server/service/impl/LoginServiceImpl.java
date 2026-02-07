package com.cs.copy.member.server.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cs.copy.system.server.config.prop.AppProperties;
import com.cs.sp.util.StringUtil;
import com.cs.copy.global.constants.Gkey;
import com.cs.copy.member.api.entity.Login;
import com.cs.copy.member.api.entity.Member;
import com.cs.copy.member.api.enums.LoginTypeEnum;
import com.cs.copy.member.api.event.RegEvent;
import com.cs.copy.member.api.service.LoginService;
import com.cs.copy.member.server.mapper.LoginMapper;
import com.cs.copy.member.server.mapper.MemberMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author gpthk
 * @since 2025-02-20
 */
@Slf4j
@Service
public class LoginServiceImpl extends ServiceImpl<LoginMapper, Login> implements LoginService {

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private AppProperties appProperties;


    @Override
    public void addCoolDown(Login login) {
        // 如果是二次绑定，就冻结1天
        int num = getBaseMapper().countBinded(login);
        if (num > 0) {
            Member cooldown = new Member();
            cooldown.setId(login.getUid());
            cooldown.setCoolDownAt(System.currentTimeMillis() + Gkey.DAY_MILLISECOND);
            memberMapper.updateById(cooldown);
        }
    }

    @Override
    public Pair<Member, Login> addLoginByWallet(String wallet) {
        Login login = getBaseMapper().selectOne(new QueryWrapper<Login>().lambda()
                .eq(Login::getType, LoginTypeEnum.BSC.getCode())
                .eq(Login::getAccount, wallet)
        );
        if (login != null) {
            Member member = memberMapper.selectById(login.getUid());
            return Pair.of(member, login);
        }
        Member member = new Member();
        member.setMainAccount(wallet);
        member.setNickname(StringUtil.senseWallet(wallet));
        member.setRegAt(System.currentTimeMillis());
        member.setCanWithdraw(appProperties.getRegCanWithdraw());
        memberMapper.insert(member);
        login = new Login();
        login.setType(LoginTypeEnum.BSC.getCode());
        login.setUid(member.getId());
        login.setAccount(wallet);
        login.setBindAt(member.getRegAt());
        getBaseMapper().insert(login);
        SpringUtil.publishEvent(new RegEvent(this, member, login));
        return Pair.of(member, login);
    }
}
