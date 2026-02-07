package com.cs.copy.asset.api.event;

import com.cs.copy.asset.api.entity.WithdrawFlow;
import com.cs.copy.evm.api.entity.ChainWork;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author fiona
 * @date 2024/10/3 16:43
 */
@Getter
public class WithdrawEvent extends ApplicationEvent {
    private WithdrawFlow withdrawFlow;
    private ChainWork chainWork;

    public WithdrawEvent(Object source, WithdrawFlow withdrawFlow, ChainWork chainWork) {
        super(source);
        this.withdrawFlow = withdrawFlow;
        this.chainWork = chainWork;
    }
}
