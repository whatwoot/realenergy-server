package com.cs.copy.member.server.listener;

import cn.hutool.core.util.StrUtil;
import com.cs.web.spring.helper.hashids.HashidsHelper;
import com.cs.copy.chain.api.service.ChainAddressService;
import com.cs.copy.member.api.entity.Member;
import com.cs.copy.member.api.event.BindEvent;
import com.cs.copy.member.api.event.RegEvent;
import com.cs.copy.member.api.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * @author fiona
 * @date 2024/12/15 00:16
 */
@Slf4j
@Component
public class MemberListener {

    @Autowired
    private HashidsHelper hashidsHelper;

    @Autowired
    private MemberService memberService;

    @Autowired
    private ChainAddressService chainAddressService;

    /**
     * 注册后,设置邀请码
     *
     * @param e
     */
    @TransactionalEventListener
    @Order(0)
    @Async
    public void reg(RegEvent e) {
        Member member = e.getMember();
        log.info("REG-CODE {}", member.getId());
        // 1、添加邀请码
        String encode = hashidsHelper.encode(member.getId());
        Member update = new Member();
        update.setId(member.getId());
        update.setInviteCode(encode);
        memberService.updateById(update);
    }


    /**
     * 绑定邀请人事件
     *
     * @param e
     */
    @TransactionalEventListener
    @Async
    public void bind(BindEvent e) {
        Member member = e.getMember();
        log.info("Bind {}=>{}", member.getId(), member.getPid());
        memberService.addBindInvite(member);
    }

    @TransactionalEventListener
    @Async
    public void onBind(BindEvent e) {
        Member member = e.getMember();
        memberService.updateEndWizard(member.getId());
    }
}
