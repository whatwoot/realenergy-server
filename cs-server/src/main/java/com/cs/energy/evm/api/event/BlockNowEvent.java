package com.cs.energy.evm.api.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author fiona
 * @date 2024/5/30 22:05
 */
@Getter
public class BlockNowEvent extends ApplicationEvent {
    private Long blockNo;

    public BlockNowEvent(Object source, Long blockNo) {
        super(source);
        this.blockNo = blockNo;
    }
}
