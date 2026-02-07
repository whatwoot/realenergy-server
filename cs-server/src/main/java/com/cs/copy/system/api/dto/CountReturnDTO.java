package com.cs.copy.system.api.dto;

import com.cs.sp.common.base.BaseVO;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author fiona
 * @date 2024/3/13 16:45
 */
@Data
public class CountReturnDTO extends BaseVO {
    private Long uid;
    private BigDecimal amount;
}
