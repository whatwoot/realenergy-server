package com.cs.copy.system.api.dto;

import com.cs.sp.common.base.BaseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fiona
 * @date 2024/10/15 00:11
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SseDTO extends BaseDTO {
    private Long uid;
    private String name;
    private String msg;
}
