package com.cs.energy.asset.api.event;

import com.cs.energy.asset.api.entity.AssetFlow;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @authro fun
 * @date 2025/5/27 02:40
 */
@Getter
public class AssetRefundEvent extends ApplicationEvent {
    private final AssetFlow assetFlow;

    public AssetRefundEvent(Object source, AssetFlow assetFlow) {
        super(source);
        this.assetFlow = assetFlow;
    }
}
