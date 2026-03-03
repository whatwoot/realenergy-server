package com.cs.energy.system.api.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @authro fun
 * @date 2025/10/24 21:46
 */
@AllArgsConstructor
@Getter
public class AfterBuildCacheEvent {
    private transient Object source;
}
