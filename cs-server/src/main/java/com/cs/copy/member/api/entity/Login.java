package com.cs.copy.member.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cs.copy.member.api.enums.LoginTypeEnum;
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
 * @since 2025-02-20
 */
@Getter
@Setter
@TableName("u_login")
@Schema(name = "Login", description = "")
public class Login extends BaseDO {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "UID")
    private Long uid;

    /**
     * @see LoginTypeEnum
     */
    @Schema(description = "类型")
    private Byte type;

    @Schema(description = "账号")
    private String account;

    @Schema(description = "密钥")
    private String secret;

    @Schema(description = "启用。1=是,0=否")
    private Byte status;
    @Schema(description = "前端展示。1=是,0=否")
    private Byte showed;

    @Schema(description = "盐值")
    private String salt;

    @Schema(description = "备注")
    private String memo;

    @Schema(description = "绑定时间")
    private Long bindAt;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "更新时间")
    private Date updateTime;

    @TableLogic(delval = "id")
    private Long deleted;
}
