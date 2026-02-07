package com.cs.web.base;

import com.cs.sp.common.base.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Range;


/**
 * @author sb
 * @date 2024/7/29 15:44
 */
@Data
public class BaseCursorRequest extends BaseRequest {
    @Schema(description = "每页记录数")
    @Range(min = 1, max = 100, message = "chk.common.valueRange")
    private Integer pageSize = 10;
}
