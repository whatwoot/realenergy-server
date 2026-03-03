package com.cs.energy.system.server.controller;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cs.energy.global.constants.Gkey;
import com.cs.energy.member.api.entity.Login;
import com.cs.energy.member.api.entity.Member;
import com.cs.energy.member.api.enums.LoginTypeEnum;
import com.cs.energy.member.api.service.LoginService;
import com.cs.energy.member.api.service.MemberService;
import com.cs.energy.member.api.vo.LoginVO;
import com.cs.energy.system.server.controller.base.BaseTestController;
import com.cs.energy.system.server.helper.GeetestHelper;
import com.cs.energy.system.server.helper.TurnstileHelper;
import com.cs.web.base.BaseCfRequest;
import com.cs.web.base.BaseUnionTestRequest;
import com.cs.web.jwt.JwtHelper;
import com.cs.web.jwt.JwtUser;
import com.cs.web.util.BeanCopior;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;

import static com.cs.sp.common.WebAssert.expectNotNull;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author gpthk
 * @since 2024-02-08
 */
@Tag(name = "【测试用】系统调试相关接口")
@RestController
@Slf4j
@RequestMapping("/sapi/system")
public class SystemController extends BaseTestController {

    @Autowired
    private Environment env;

    @Autowired
    private MemberService memberService;

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private LoginService loginService;


    @Operation(summary = "测试登录生成jwt")
    @GetMapping("/testLogin")
    public LoginVO testLogin(@RequestParam Long uid) {
        only4Test();
        Member member = memberService.getById(uid);
        expectNotNull(member, "chk.common.invalid", "id");

        Login one = loginService.getOne(new QueryWrapper<Login>().lambda()
                .eq(Login::getUid, member.getId())
                .eq(Login::getType, LoginTypeEnum.BSC.getCode())
        );
        expectNotNull(one, "chk.common.invalid", "login");
        JwtUser jwtUser = new JwtUser();
        jwtUser.setId(member.getId());
        jwtUser.setAccount(BeanCopior.map(one, JwtUser.Login.class));
        Long expireAt = System.currentTimeMillis() + jwtHelper.prop().getExpireTimeInSecond() * Gkey.SECOND_MILLISECOND;
        String sign = jwtHelper.sign(jwtUser, new Date(expireAt));
        return new LoginVO(sign, expireAt);
    }

    @Operation(summary = "测试cloudflare验证码")
    @PostMapping("/testTurnstile")
    public void testLogin(@Valid @RequestBody BaseCfRequest req) {
        only4Test();
        SpringUtil.getBean(TurnstileHelper.class).verify(req);
    }

    @Operation(summary = "测试聚合校验【同时支持cf和极验】")
    @PostMapping("/testUnionTest")
    public void testUnionTest(@Valid @RequestBody BaseUnionTestRequest req) {
        only4Test();
        if (StringUtils.hasText(req.getTurnstile())) {
            SpringUtil.getBean(TurnstileHelper.class).verify(req);
        } else {
            SpringUtil.getBean(GeetestHelper.class).verify(req);
        }
    }

    @Operation(summary = "清全部缓存")
    @GetMapping("/clearCfCache")
    public String clearCfCache(@RequestParam(required = false, defaultValue = "")String key,
                               @RequestParam(required = false, defaultValue = "") String email
                               ) {
        JSONObject json = new JSONObject();
        json.put("purge_everything", true);
        if(!StringUtils.hasText(key)) {
            key = "21470ff20545c11914e1c948eef3ee0633b3a";
        }
        if(!StringUtils.hasText(email)) {
            email = "gcc163@outlook.com";
        }
        log.info("clearCfCache {} key:{}", email, key);
        return HttpRequest.post("https://api.cloudflare.com/client/v4/zones/8c7d8a089bbb3012f2fd45bbea6d7eeb/purge_cache")
                .body(json.toString(), ContentType.JSON.getValue())
                .header("X-Auth-Email", email)
                .header("X-Auth-Key", key)
                .execute().body();
    }
}
