package com.cs.energy.member.api.event;

import com.cs.energy.member.api.entity.Login;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @authro fun
 * @date 2025/6/13 13:04
 */
@Getter
public class BindOtpEvent extends ApplicationEvent {
    private Login login;

    public BindOtpEvent(Object source, Login login) {
        super(source);
        this.login = login;
    }
}
