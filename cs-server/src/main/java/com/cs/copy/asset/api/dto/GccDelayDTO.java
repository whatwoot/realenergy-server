package com.cs.copy.asset.api.dto;

import com.cs.sp.common.base.BaseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * @authro fun
 * @date 2025/10/2 19:21
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GccDelayDTO extends BaseDTO {
    private Long id;
    private Long time;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        GccDelayDTO that = (GccDelayDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
