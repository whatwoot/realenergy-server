package com.cs.energy.evm.api.event;

import com.cs.energy.evm.api.entity.DecPriceHis;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @authro fun
 * @date 2026/1/4 19:10
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DecPriceHisAddEvent {
    private transient Object source;
    private DecPriceHis add;
}
