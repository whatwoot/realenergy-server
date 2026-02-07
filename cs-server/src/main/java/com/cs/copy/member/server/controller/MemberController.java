package com.cs.copy.member.server.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cs.copy.global.constants.CacheKey;
import com.cs.copy.member.api.entity.Login;
import com.cs.copy.member.api.entity.Member;
import com.cs.copy.member.api.request.BindRequest;
import com.cs.copy.member.api.request.InviteListRequest;
import com.cs.copy.member.api.request.MemberEditRequest;
import com.cs.copy.member.api.service.LoginService;
import com.cs.copy.member.api.service.MemberService;
import com.cs.copy.member.api.vo.*;
import com.cs.copy.system.server.config.prop.AppProperties;
import com.cs.copy.system.server.controller.base.BaseUnionTestController;
import com.cs.sp.enums.YesNoByteEnum;
import com.cs.web.annotation.LoginRequired;
import com.cs.web.annotation.RateLimiter;
import com.cs.web.jwt.JwtUser;
import com.cs.web.jwt.JwtUserHolder;
import com.cs.web.spring.helper.GoogleAuthenticator;
import com.cs.web.spring.helper.hashids.HashidsHelper;
import com.cs.web.util.BeanCopior;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

import static com.cs.sp.common.WebAssert.*;


/**
 * <p>
 * 用户-成员表 前端控制器
 * </p>
 *
 * @author gpthk
 * @since 2024-09-29
 */
@Slf4j
@Tag(name = "用户")
@RestController
@RequestMapping("/api/member")
public class MemberController extends BaseUnionTestController {

    @Autowired
    private HashidsHelper hashidsHelper;

    @Autowired
    private MemberService memberService;

    @Autowired
    private LoginService loginService;

    @Autowired
    private AppProperties appProperties;


    @Operation(summary = "用户会话信息（JWT中包含的信息）")
    @LoginRequired
    @GetMapping("/info")
    public JwtUser info() {
        return JwtUserHolder.get();
    }

    @Operation(summary = "用户详细信息")
    @LoginRequired
    @GetMapping("/detail")
    public UserDetailVO detail() {
        JwtUser jwtUser = JwtUserHolder.get();
        Member member = memberService.getById(jwtUser.getId());
        List<Login> list = loginService.list(new QueryWrapper<Login>().lambda()
                .eq(Login::getUid, member.getId())
        );
        return BeanCopior.map(member, UserDetailVO.class, dest -> {
            dest.setAccounts(BeanCopior.mapList(list, JwtUser.Login.class));
        });
    }

    //    @GetMapping("/ofInvite")
    @Operation(summary = "邀请人信息")
    @RateLimiter(key = CacheKey.OF_INVITE_LIMIT)
    public UserInviteVO ofInvite(@RequestParam String inviteCode) {
        Long uid = hashidsHelper.decode(inviteCode);
        isNotNull(uid, "chk.user.invalidInviteCode");
        Member member = memberService.getById(uid);
        return BeanCopior.map(member, UserInviteVO.class);
    }

    @Operation(summary = "邀请列表")
    @LoginRequired
    @GetMapping("/inviteList")
    public Page<MemberListVO> directInviteNum(@Valid InviteListRequest req) {
        Page<Member> page = new Page<>(req.getPageNo(), req.getPageSize());
        Page<Member> pageList = memberService.page(page, Wrappers.lambdaUpdate(Member.class)
                .eq(Member::getPid, JwtUserHolder.get().getId())
                .orderByDesc(Member::getTeamPerformance, Member::getPerformance)
        );
        return BeanCopior.mapPage(pageList, MemberListVO.class);
    }

    @Operation(summary = "生成谷歌验证码")
    @LoginRequired
    public GoogleCodeVO genOtpCode() {
        JwtUser jwtUser = JwtUserHolder.get();
        String account = jwtUser.getAccount().getAccount();
        String secretKey = GoogleAuthenticator.generateSecretKey();
        GoogleCodeVO googleCodeVO = new GoogleCodeVO();
        googleCodeVO.setOtpSecret(secretKey);
        googleCodeVO.setOtpSecretUrl(GoogleAuthenticator.getOtpAuthUrl(secretKey, account, appProperties.getGaIssuer()));
        return googleCodeVO;
    }


    @Operation(summary = "邀请统计")
    @LoginRequired
    @GetMapping("/inviteSummary")
    public InviteSummary inviteSummary() {
        Long id = JwtUserHolder.get().getId();
        return memberService.groupInviteSummary(id);
    }

//    @Operation(summary = "更新用户信息")
//    @LoginRequired
//    @PostMapping("/edit")
    public UserDetailVO edit(@RequestParam(required = false, defaultValue = "0") Byte fetch,
                             @Valid @RequestBody MemberEditRequest req) {
        JwtUser jwtUser = JwtUserHolder.get();
        boolean hasValue = false;
        for (Field field : FieldUtils.getAllFields(req.getClass())) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            field.setAccessible(true);
            try {
                if (FieldUtils.readField(field, req, true) != null) {
                    hasValue = true;
                    break;
                }
            } catch (IllegalAccessException e) {
            }
        }
        expect(hasValue, "chk.member.editNonNull");
        Member member = BeanCopior.map(req, Member.class);
        member.setId(jwtUser.getId());
        memberService.updateById(member);
        if (YesNoByteEnum.YES.eq(fetch)) {
            member = memberService.getById(member.getId());
        }
        return BeanCopior.map(member, UserDetailVO.class);
    }

    @Operation(summary = "绑定邀请")
    @LoginRequired
    @PostMapping("/bindInvite")
    public UserBindVO bindInvite(@Valid @RequestBody BindRequest req) {
        String code = req.getInviteCode().trim().toUpperCase();
        Long pid = hashidsHelper.decode(code);
        expectNotNull(pid, "chk.reg.invalidInviteCode");
        Member invitor = memberService.getById(pid);
        expectNotNull(invitor, "chk.reg.invalidInviteCode");
        expect(YesNoByteEnum.YES.eq(invitor.getStatus()), "chk.reg.invalidInviteCode");
        Long uid = JwtUserHolder.get().getId();
        expect(!uid.equals(pid), "chk.bind.notSelf");
        Member member = new Member();
        member.setId(uid);
        member.setPid(pid);
        expectGt0(memberService.updateBindInvite(member), "chk.bind.bindFail");
        return BeanCopior.map(member, UserBindVO.class);
    }

    @Operation(summary = "各等级用户数量")
//    @GetMapping("/levelsNum")
    public Map levelsNum() {
        return memberService.listLevelsNum();
    }

    @Operation(summary = "完成向导")
    @LoginRequired
    @PostMapping("/wizardEnd")
    public Integer wizardEnd() {
        return memberService.updateEndWizard(JwtUserHolder.get().getId());
    }
}
