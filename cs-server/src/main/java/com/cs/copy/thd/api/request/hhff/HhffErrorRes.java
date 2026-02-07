package com.cs.copy.thd.api.request.hhff;

import com.cs.sp.common.base.BaseVO;
import lombok.Data;

/**
 * @authro fun
 * @date 2025/7/10 01:17
 */
@Data
public class HhffErrorRes extends BaseVO {
    private String code;
    private String desc;
}
