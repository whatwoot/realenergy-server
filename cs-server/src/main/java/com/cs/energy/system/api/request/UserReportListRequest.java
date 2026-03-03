package com.cs.energy.system.api.request;

import com.cs.sp.common.base.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @authro fun
 * @date 2025/5/25 14:20
 */
@Data
@Schema(description = "最近业绩列表查询【用于首次同步业绩列表】")
public class UserReportListRequest extends BaseRequest {
    private Long nextId;
    private Integer pageSize = 5000;
}
