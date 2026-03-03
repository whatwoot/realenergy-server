package com.cs.energy.member.api.event;

import com.cs.energy.member.api.entity.Login;
import com.cs.energy.member.api.entity.Member;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author fiona
 * @date 2024/9/29 21:09
 */
@Getter
public class RegEvent extends ApplicationEvent {
    private Member member;
    private Login login;

    public RegEvent(Object source, Member member, Login login) {
        super(source);
        this.member = member;
        this.login = login;
    }
}
