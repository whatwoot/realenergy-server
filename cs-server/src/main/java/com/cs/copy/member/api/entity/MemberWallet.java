package com.cs.copy.member.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cs.sp.common.base.BaseDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author gpthk
 * @since 2025-02-24
 */
@Getter
@Setter
@TableName("u_member_wallet")
@Schema(name = "MemberWallet", description = "")
public class MemberWallet extends BaseDO{

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "类型:1=充值地址,2=提现地址")
    private Byte type;

    @Schema(description = "链")
    private String chain;

    @Schema(description = "UID")
    private Long uid;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "钱包地址")
    private String wallet;

    @Schema(description = "备注")
    private String memo;

    private Integer seq;
    @Schema(description = "权重")
    private Integer weight;

    @Schema(description = "创建于")
    private Long createAt;

    private Date createTime;

    private Date updateTime;

    @TableLogic(delval = "id")
    private Long deleted;
}
