package com.cs.energy.member.server.controller;

import com.cs.energy.global.constants.Gkey;
import com.cs.energy.system.server.config.prop.AppProperties;
import com.cs.sp.constant.Constant;
import com.cs.web.base.BaseController;
import com.cs.web.jwt.JwtHelper;
import com.cs.web.jwt.JwtProperties;
import com.cs.web.jwt.JwtUser;
import com.cs.web.util.BeanCopior;
import com.cs.energy.evm.api.util.EthSignUtil;
import com.cs.energy.member.api.entity.Login;
import com.cs.energy.member.api.entity.Member;
import com.cs.energy.member.api.request.LoginByWalletRequest;
import com.cs.energy.member.api.service.LoginService;
import com.cs.energy.member.api.vo.LoginVO;
import com.cs.energy.member.api.vo.UserNonceVO;
import com.cs.energy.member.server.util.LoginUtil;
import com.cs.energy.system.api.annotation.MaintainCheck;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.web.bind.annotation.*;
import org.web3j.crypto.WalletUtils;

import javax.validation.Valid;

import java.util.StringJoiner;

import static com.cs.sp.common.WebAssert.isTrue;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author gpthk
 * @since 2025-02-20
 */
@Tag(name = "认证中心")
@RestController
@RequestMapping("/api/auth")
public class AuthController extends BaseController {

    @Autowired
    private Environment env;

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private LoginService loginService;

    @Autowired
    private AppProperties appProperties;

    @Operation(summary = "签名nonce")
    @GetMapping("/nonce")
    public UserNonceVO nonce() {
        String nonce = LoginUtil.genPayload(jwtProperties.getSignKey());
        return new UserNonceVO(nonce, System.currentTimeMillis());
    }

    @Operation(summary = "钱包登录")
    @PostMapping("/login")
    @MaintainCheck
    public LoginVO login(@Valid @RequestBody LoginByWalletRequest req) {
        isTrue(WalletUtils.isValidAddress(req.getAddr()), "chk.common.invalid", "addr");
        boolean skipSign = !env.acceptsProfiles(Profiles.of(Constant.PROD)) && "test".equalsIgnoreCase(req.getSign());
        if (!skipSign) {
            LoginUtil.checkPayload(req.getNonce(), req.getTime(), jwtProperties.getSignKey());
            StringJoiner sj = new StringJoiner("\n");
            sj.add(appProperties.getWelcomeStr());
            sj.add("");
            sj.add(req.getAddr());
            sj.add("Login at");
            sj.add(req.getTime().toString());
            sj.add("Nonce:");
            sj.add(req.getNonce());
            EthSignUtil.checkFullSign(req.getAddr(), sj.toString(), req.getSign());
        }
        String wallet = req.getAddr().toLowerCase();
        Pair<Member, Login> pair = loginService.addLoginByWallet(wallet);
        JwtUser jwtUser = BeanCopior.map(pair.getLeft(), JwtUser.class);
        jwtUser.setAccount(BeanCopior.map(pair.getRight(), JwtUser.Login.class));
        String sign = jwtHelper.sign(jwtUser);
        Long expireAt = System.currentTimeMillis() + jwtHelper.prop().getExpireTimeInSecond() * Gkey.SECOND_MILLISECOND;
        return new LoginVO(sign, expireAt);
    }
}
