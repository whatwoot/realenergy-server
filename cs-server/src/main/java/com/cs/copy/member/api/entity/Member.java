package com.cs.copy.member.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cs.sp.common.base.BaseDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 用户-成员表
 * </p>
 *
 * @author gpthk
 * @since 2024-09-29
 */
@Getter
@Setter
@TableName("u_member")
@Schema(name = "Member", description = "用户-成员表")
public class Member extends BaseDO {

    @Schema(description = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "主账户")
    private String mainAccount;

    @Schema(description = "昵称。如ens")
    private String nickname;

    @Schema(description = "头像url")
    private String photoUrl;

    @Schema(description = "邀请人id")
    private Long pid;

    @Schema(description = "邀请码")
    private String inviteCode;

    @Schema(description = "国家代码")
    private Integer countryCode;
    private String country;

    @Schema(description = "是否代理。1=是,0=否")
    private Byte agented;
    @Schema(description = "代理地区")
    private Integer agentCode;

    @Schema(description = "全名")
    private String fullName;
    @Schema(description = "姓")
    private String familyName;
    @Schema(description = "名")
    private String givenName;

    @Schema(description = "国家代码")
    private Integer mobileCode;
    private String mobile;

    @Schema(description = "性别。1=男,0=女")
    private Byte gender;

    @Schema(description = "家庭住址")
    private String address;

    @Schema(description = "渠道社区")
    private String community;

    @Schema(description = "生日")
    private Integer birthday;

    @Schema(description = "状态。1=正常")
    private Byte status;
    @Schema(description = "货币")
    private String currency;
    @Schema(description = "完成注册向导。1=是,0=否")
    private Byte wizardEnd;
    @Schema(description = "完成注册于")
    private Long wizardEndAt;

    @Schema(description = "有效用户。1=是,0=否")
    private Byte valid;
    @Schema(description = "开始有效用户于")
    private Long validAt;

    @Schema(description = "注册时间。国际化使用")
    private Long regAt;
    @Schema(description = "冷却时间")
    private Long coolDownAt;

    @Schema(description = "注册来源")
    private String regSource;

    @Schema(description = "团队级别")
    private Integer levelId;

    @Schema(description = "邀请级别")
    private Integer inviteLevelId;

    @Schema(description = "备注")
    private String memo;

    @Schema(description = "可提现。1=是,0=否")
    private Byte canWithdraw;
    @Schema(description = "可支付。1=是,0=否")
    private Byte canPay;
    @Schema(description = "创世节点。1=是,0=否")
    private Byte genesis;

    @Schema(description = "邀请总人数")
    private Integer inviteNum;
    @Schema(description = "直推总人数")
    private Integer directInviteNum;
    @Schema(description = "有效直推总人数")
    private Integer validDirectInviteNum;

    @Schema(description = "个人业绩")
    private BigDecimal performance;
    @Schema(description = "冻结业绩")
    private BigDecimal frozenPerformance;
    @Schema(description = "团队业绩")
    private BigDecimal teamPerformance;
    @Schema(description = "业绩更新于")
    private Long performanceUpdateAt;
    @Schema(description = "个人奖池")
    private BigDecimal prizeAmount;
    @Schema(description = "最近奖池派发日")
    private Integer prizeBonusDay;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "更新时间")
    private Date updateTime;

    @Schema(description = "是否删除。0=未删除，其他=删除")
    @TableLogic(delval = "id")
    private Long deleted;
}
