package com.cs.energy.member.api.event;

import com.cs.energy.member.api.entity.Login;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author fiona
 * @date 2025/2/28 18:09
 */
@Getter
public class SetPinEvent extends ApplicationEvent {
    private Login login;

    public SetPinEvent(Object source, Login login) {
        super(source);
        this.login = login;
    }
}
