package com.cs.energy.system.api.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @authro fun
 * @date 2025/12/28 18:23
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PropRefreshEvent {
    private transient Object source;
}
