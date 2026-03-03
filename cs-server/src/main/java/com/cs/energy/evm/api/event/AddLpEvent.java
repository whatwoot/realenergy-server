package com.cs.energy.evm.api.event;

import com.cs.energy.evm.api.entity.ChainWork;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author fiona
 * @date 2024/12/21 04:45
 */
@Getter
public class AddLpEvent extends ApplicationEvent {
    private ChainWork chainWork;

    public AddLpEvent(Object source, ChainWork chainWork) {
        super(source);
        this.chainWork = chainWork;
    }
}
