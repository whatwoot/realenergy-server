package com.cs.energy.evm.api.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class RefreshAddrBalanceEvent extends ApplicationEvent {
    public RefreshAddrBalanceEvent(Object source) {
        super(source);
    }
}
