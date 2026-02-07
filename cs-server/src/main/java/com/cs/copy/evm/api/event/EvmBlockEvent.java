package com.cs.copy.evm.api.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.web3j.protocol.core.methods.response.EthBlock;

/**
 * @author fiona
 * @date 2024/5/30 22:05
 */
@Getter
public class EvmBlockEvent extends ApplicationEvent {
    private EthBlock.Block block;

    public EvmBlockEvent(Object source, EthBlock.Block block) {
        super(source);
        this.block = block;
    }
}
