package com.cs.energy.evm.api.dto;

import com.cs.energy.evm.api.entity.ChainWork;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * @authro fun
 * @date 2025/6/30 15:15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChainWorkQueueDTO {
    private Long id;
    private ChainWork chainWork;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ChainWorkQueueDTO that = (ChainWorkQueueDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
