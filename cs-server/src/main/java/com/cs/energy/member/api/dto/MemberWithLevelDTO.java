package com.cs.energy.member.api.dto;

import com.cs.energy.member.api.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * 用户-成员表
 * </p>
 *
 * @author gpthk
 * @since 2024-09-29
 */
@Data
public class MemberWithLevelDTO extends Member {
    @Schema(description = "第几层")
    private Integer n;
}
