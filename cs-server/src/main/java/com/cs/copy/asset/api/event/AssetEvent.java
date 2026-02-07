package com.cs.copy.asset.api.event;

import com.cs.copy.asset.api.entity.AssetFlow;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @authro fun
 * @date 2025/5/27 02:40
 */
@Getter
public class AssetEvent extends ApplicationEvent {
    private final AssetFlow assetFlow;

    public AssetEvent(Object source, AssetFlow assetFlow) {
        super(source);
        this.assetFlow = assetFlow;
    }
}
