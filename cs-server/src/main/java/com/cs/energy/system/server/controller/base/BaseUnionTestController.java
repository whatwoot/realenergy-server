package com.cs.energy.system.server.controller.base;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cs.energy.member.api.entity.Login;
import com.cs.energy.member.api.enums.LoginTypeEnum;
import com.cs.energy.member.api.service.LoginService;
import com.cs.energy.system.server.helper.GeetestHelper;
import com.cs.energy.system.server.helper.TurnstileHelper;
import com.cs.web.base.BaseOtpRequest;
import com.cs.web.base.BaseUnionTestRequest;
import com.cs.web.spring.helper.GoogleAuthenticator;
import com.cs.web.spring.helper.aeshelper.AesHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import static com.cs.sp.common.WebAssert.expect;
import static com.cs.sp.common.WebAssert.expectNotNull;

/**
 * @author fiona
 * @date 2025/2/27 12:46
 */
public class BaseUnionTestController extends BasePinController {

    @Autowired
    protected LoginService loginService;
    @Autowired
    private AesHelper aesHelper;

    protected void verifyCaptcha(BaseUnionTestRequest req){
        if(StringUtils.hasText(req.getTurnstile())){
            SpringUtil.getBean(TurnstileHelper.class).verify(req);
        }else{
            SpringUtil.getBean(GeetestHelper.class).verify(req);
        }
    }

    protected boolean checkOtp(BaseOtpRequest req, Long uid){
        Login login = loginService.getOne(new QueryWrapper<Login>().lambda()
                .eq(Login::getUid, uid)
                .eq(Login::getType, LoginTypeEnum.OTP.getCode())
        );
        expectNotNull(login, "chk.common.otpNotExists");
        return checkOtp(req.getOtpCode(), aesHelper.decrypt(login.getSecret()));
    }

    protected boolean checkOtp(String otp, String secret){
        return GoogleAuthenticator.verifyCode(secret, otp, 2);
    }

    protected void verifyOtp(BaseOtpRequest req, Long uid){
        boolean ok = checkOtp(req, uid);
        expect(ok, "chk.common.otpFail");
    }
}
