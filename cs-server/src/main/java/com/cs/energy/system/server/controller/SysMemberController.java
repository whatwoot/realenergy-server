
package com.cs.energy.system.server.controller;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cs.energy.global.constants.CacheKey;
import com.cs.energy.member.api.entity.Login;
import com.cs.energy.member.api.entity.Member;
import com.cs.energy.member.api.enums.LoginTypeEnum;
import com.cs.energy.member.api.service.LoginService;
import com.cs.energy.member.api.service.MemberService;
import com.cs.energy.member.api.vo.UserDetailVO;
import com.cs.sp.common.BeanCopior;
import com.cs.web.jwt.JwtUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

import static com.cs.sp.common.WebAssert.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author gpthk
 * @since 2024-02-08
 */
@Tag(name = "【生产环境不对外】运营接口")
@RestController
@Slf4j
@RequestMapping("/sapi/sys")
public class SysMemberController {

    @Autowired
    private Environment env;

    @Autowired
    private MemberService memberService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Operation(summary = "设置用户等级")
    @GetMapping("/member/setLevel")
    public Boolean setLevel(@RequestParam Long uid,
                            @RequestParam Integer level) {
        Member member = new Member();
        member.setId(uid);
        member.setLevelId(level);
        return memberService.updateById(member);
    }


    @Operation(summary = "绑定邀请")
    @GetMapping("/member/bindInvite")
    public void bindInvite(@RequestParam Long uid,
                           @RequestParam Long pid) {
        Member invitor = memberService.getById(pid);
        expectNotNull(invitor, "chk.reg.invalidInviteCode");
        expect(!uid.equals(pid), "chk.bind.notSelf");
        Member member = new Member();
        member.setId(uid);
        member.setPid(pid);
        expectGt0(memberService.updateBindInvite(member), "chk.bind.bindFail");
    }

    @Operation(summary = "账号查信息。type: 3=钱包地址")
    @GetMapping("/member/account")
    public UserDetailVO memberAccount(@RequestParam String account,
                                      @RequestParam(required = false, defaultValue = "3") String type) {
        LoginTypeEnum loginTypeEnum = LoginTypeEnum.of(Byte.valueOf(type));
        isNotNull(loginTypeEnum, "chk.common.invalid", "type");
        Login one = SpringUtil.getBean(LoginService.class).getOne(Wrappers.lambdaQuery(Login.class)
                .eq(Login::getAccount, account)
                .eq(Login::getType, loginTypeEnum.getCode())
        );
        isNotNull(one, "chk.common.invalid", "account");
        Member member = SpringUtil.getBean(MemberService.class).getById(one.getUid());
        UserDetailVO user = BeanCopior.map(member, UserDetailVO.class);

        List<Login> list = SpringUtil.getBean(LoginService.class).list(Wrappers.lambdaQuery(Login.class)
                .eq(Login::getUid, one.getUid())
        );
        user.setAccounts(BeanCopior.mapList(list, JwtUser.Login.class));
        return user;
    }

    @Operation(summary = "节点用户id")
    @GetMapping("/member/genesisList")
    public Set<String> genesisList() {
        BoundSetOperations<String, String> genesisUids = stringRedisTemplate.boundSetOps(CacheKey.GENESSIS_UIDS);
        return genesisUids.members();
    }

    @Operation(summary = "更换登录钱包")
    @GetMapping("/member/changeLoginWallet")
    public Member changeLoginWallet(@RequestParam String old, @RequestParam String newer) {
        return memberService.updateChangeWallet(old, newer);
    }
}


