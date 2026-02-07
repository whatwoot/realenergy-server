package com.cs.sp.common.base;

import lombok.Data;

/**
 * @author sb
 * @date 2023/9/27 16:54
 */
@Data
public class BasePageVO extends BaseVO {
    /**
     * 页码
     */
    private Integer pageNo = 1;
    /**
     * 每页记录数
     */
    private Integer pageSize = 10;
}
