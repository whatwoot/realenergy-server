package com.cs.copy.evm.api.event;

import com.cs.copy.evm.api.entity.NftFlow;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author fiona
 * @date 2024/12/30 03:10
 */
@Getter
public class NftFindEvent extends ApplicationEvent {
    private NftFlow nftFlow;

    public NftFindEvent(Object source, NftFlow nftFlow) {
        super(source);
        this.nftFlow = nftFlow;
    }
}
