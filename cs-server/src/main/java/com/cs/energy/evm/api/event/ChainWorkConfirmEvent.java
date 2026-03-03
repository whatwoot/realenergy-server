package com.cs.energy.evm.api.event;

import com.cs.energy.evm.api.entity.ChainWork;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author fiona
 * @date 2024/12/24 14:09
 */
@Getter
public class ChainWorkConfirmEvent extends ApplicationEvent {
    private ChainWork chainWork;

    public ChainWorkConfirmEvent(Object source, ChainWork chainWork) {
        super(source);
        this.chainWork = chainWork;
    }
}
