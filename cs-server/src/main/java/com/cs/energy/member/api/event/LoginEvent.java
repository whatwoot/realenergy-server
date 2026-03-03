package com.cs.energy.member.api.event;

import com.cs.energy.member.api.entity.Member;
import com.cs.energy.member.api.enums.LoginChannelEnum;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author fiona
 * @date 2024/9/29 21:09
 */
@Getter
public class LoginEvent extends ApplicationEvent {
    private Member member;
    private LoginChannelEnum channel;

    public LoginEvent(Object source, Member member, LoginChannelEnum channel) {
        super(source);
        this.member = member;
        this.channel = channel;
    }
}
