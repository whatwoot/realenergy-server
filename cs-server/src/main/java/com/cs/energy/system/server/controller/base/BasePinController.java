package com.cs.energy.system.server.controller.base;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cs.web.base.BaseController;
import com.cs.web.base.BasePinRequest;
import com.cs.web.spring.helper.argon2.Argon2Helper;
import com.cs.web.spring.helper.rsahelper.RsaHelper;
import com.cs.energy.member.api.entity.Login;
import com.cs.energy.member.api.enums.LoginTypeEnum;
import com.cs.energy.member.api.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;

import static com.cs.sp.common.WebAssert.*;

/**
 * @author fiona
 * @date 2025/2/27 12:46
 */
public class BasePinController extends BaseController {

    @Autowired
    protected RsaHelper rsaHelper;
    @Autowired
    protected LoginService loginService;

    protected void verifyPin(BasePinRequest request, Long uid) {
        Login login = loginService.getOne(new QueryWrapper<Login>().lambda()
                .eq(Login::getUid, uid)
                .eq(Login::getType, LoginTypeEnum.PIN.getCode())
        );
        expectNotNull(login, "chk.pay.pinNotExists");
        String decrypt = rsaHelper.decrypt(request.getSecret());
        isNotBlank(decrypt, "chk.pay.pinIncorrect");
        String encrypt = SpringUtil.getBean(Argon2Helper.class).encrypt(decrypt, login.getSalt());
        expect(login.getSecret().equals(encrypt), "chk.pay.pinIncorrect");
    }
}
