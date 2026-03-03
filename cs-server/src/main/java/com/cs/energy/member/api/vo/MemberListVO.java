package com.cs.energy.member.api.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.cs.sp.common.base.BaseVO;
import com.cs.sp.serializer.MoneySerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * <p>
 * 用户-成员表
 * </p>
 *
 * @author gpthk
 * @since 2024-09-29
 */
@Data
@Schema(description = "用户列表")
public class MemberListVO extends BaseVO {

    @Schema(description = "id")
    private Long id;

    @Schema(description = "主账号")
    private String mainAccount;

    @Schema(description = "昵称。如ens")
    private String nickname;

    @Schema(description = "头像url")
    private String photoUrl;

    @Schema(description = "邀请人id")
    private Long pid;

    @Schema(description = "邀请码")
    private String inviteCode;

    @Schema(description = "注册时间。国际化使用")
    private Long regAt;

    @Schema(description = "邀请总人数")
    private Integer inviteNum;

    @Schema(description = "个人业绩")
    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal performance;
    @Schema(description = "团队业绩")
    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal teamPerformance;

    @JsonSerialize(using= MoneySerializer.class)
    @Schema(description = "贡献的流水")
    public BigDecimal getTotalPerformance(){
        return performance.add(teamPerformance);
    }
}
