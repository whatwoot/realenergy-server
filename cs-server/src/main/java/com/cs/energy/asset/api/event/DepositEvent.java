package com.cs.energy.asset.api.event;

import com.cs.energy.asset.api.entity.AssetFlow;
import com.cs.energy.evm.api.entity.ChainWork;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author fiona
 * @date 2024/10/3 16:43
 */
@Getter
public class DepositEvent extends ApplicationEvent {
    private ChainWork chainWork;
    private AssetFlow assetFlow;

    public DepositEvent(Object source, ChainWork chainWork, AssetFlow assetFlow) {
        super(source);
        this.chainWork = chainWork;
        this.assetFlow = assetFlow;
    }
}
