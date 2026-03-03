package com.cs.energy.member.api.vo;

import com.cs.sp.common.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author fiona
 * @date 2024/9/29 21:06
 */
@Data
@Schema(description = "用户降等级信息")
public class UserLvDownVO extends BaseVO {
    private Long id;
    @Schema(description = "当前等级")
    private Integer levelId;
    @Schema(description = "原等级")
    private Integer fromLevelId;
}
