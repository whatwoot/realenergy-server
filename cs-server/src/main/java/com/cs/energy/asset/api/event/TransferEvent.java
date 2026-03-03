package com.cs.energy.asset.api.event;

import com.cs.energy.asset.api.entity.AssetFlow;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @authro fun
 * @date 2026/1/6 02:15
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TransferEvent {
    private transient  Object source;
    private List<AssetFlow> flows;
}
