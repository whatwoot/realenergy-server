package com.cs.energy.member.api.event;

import com.cs.energy.member.api.entity.Login;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @authro fun
 * @date 2025/5/25 15:53
 */
@Getter
public class BindBscEvent extends ApplicationEvent {
    private Login login;

    public BindBscEvent(Object source, Login login) {
        super(source);
        this.login = login;
    }
}
