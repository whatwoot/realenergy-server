package com.cs.copy.evm.api.event;

import com.cs.copy.evm.api.entity.BlockHis;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author fiona
 * @date 2024/5/30 22:05
 */
@Getter
public class BlockHisEvent extends ApplicationEvent {
    private BlockHis blockHis;

    public BlockHisEvent(Object source, BlockHis blockHis) {
        super(source);
        this.blockHis = blockHis;
    }
}
