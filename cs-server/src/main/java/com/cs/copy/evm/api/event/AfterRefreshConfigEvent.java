package com.cs.copy.evm.api.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @authro fun
 * @date 2025/11/24 19:34
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AfterRefreshConfigEvent {
    private transient Object source;
}
