package com.cs.copy.member.server.listener;

import cn.hutool.core.util.StrUtil;
import com.cs.copy.member.api.entity.Login;
import com.cs.copy.member.api.event.BindOtpEvent;
import com.cs.copy.member.api.event.SetPinEvent;
import com.cs.copy.member.api.service.LoginService;
import com.cs.copy.member.api.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * @author fiona
 * @date 2025/2/28 18:10
 */
@Slf4j
@Component
public class LoginListener {

    @Autowired
    private MemberService memberService;
    @Autowired
    private LoginService loginService;

    @TransactionalEventListener
    @Async
    public void setEndWizard(SetPinEvent event) {
        Login login = event.getLogin();
        try {
            memberService.updateEndWizard(login.getUid());
        } catch (Throwable e) {
            log.warn(StrUtil.format("Valid-fail: {}", login.getUid()), e);
        }
    }


    @TransactionalEventListener
    @Async
    public void onBindOtp(BindOtpEvent event) {
        Login login = event.getLogin();
        try {
            loginService.addCoolDown(login);
        } catch (Throwable e) {
            log.warn(StrUtil.format("onBindOtp: {} fail", login.getUid()), e);
        }
    }
}
