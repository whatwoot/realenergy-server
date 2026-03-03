package com.cs.energy.member.api.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.cs.sp.common.base.BaseVO;
import com.cs.sp.serializer.MoneySerializer;
import com.cs.web.jwt.JwtUser;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author fiona
 * @date 2024/9/29 21:06
 */
@Data
@Schema(description = "用户详细信息")
public class UserDetailVO extends BaseVO {
    @Schema(description = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "主账号")
    private String mainAccount;

    @Schema(description = "昵称。如ens")
    private String nickname;

    @Schema(description = "邀请人id")
    private Long pid;

    @Schema(description = "邀请码")
    private String inviteCode;

    @Schema(description = "状态。1=正常")
    private Byte status;

    @Schema(description = "有效用户。1=是,0=否")
    private Byte valid;
    @Schema(description = "开始有效用户于")
    private Long validAt;

    @Schema(description = "注册时间")
    private Long regAt;

    @Schema(description = "注册来源")
    private String regSource;

    @Schema(description = "团队级别")
    private Integer levelId;

    @Schema(description = "邀请级别")
    private Integer inviteLevelId;

    @Schema(description = "可提现。1=是,0=否")
    private Byte canWithdraw;
    @Schema(description = "节点。1=是,0=否")
    private Byte genesis;

    @Schema(description = "邀请总人数")
    private Integer inviteNum;
    @Schema(description = "直推总人数")
    private Integer directInviteNum;
    @Schema(description = "有效直推总人数")
    private Integer validDirectInviteNum;

    @Schema(description = "个人业绩")
    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal performance;
    @Schema(description = "团队业绩")
    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal teamPerformance;

    @Schema(description = "完成注册向导。1=是,0=否")
    private Byte wizardEnd;
    @Schema(description = "完成注册于")
    private Long wizardEndAt;

    @Schema(description = "账户列表")
    private List<JwtUser.Login> accounts;
}
