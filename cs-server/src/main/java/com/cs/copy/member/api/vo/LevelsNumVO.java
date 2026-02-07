package com.cs.copy.member.api.vo;

import com.cs.sp.common.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @authro fun
 * @date 2025/10/10 00:25
 */
@Data
@Schema(description = "邀请统计信息")
public class LevelsNumVO extends BaseVO {
    private Integer levelId;
    private Long num;
}
