package com.cs.energy.system.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author gpthk
 * @since 2025-10-09
 */
@Getter
@Setter
@TableName("s_apply_flow")
@Schema(name = "ApplyFlow", description = "")
public class ApplyFlow implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "用户id")
    private Long uid;

    @Schema(description = "业务")
    private String scene;

    @Schema(description = "状态。0=待处理,1=同意,2=拒绝")
    private Byte status;

    private String params;

    @Schema(description = "审核时间")
    private Long auditAt;

    @Schema(description = "审核消息")
    private String auditMsg;

    @Schema(description = "提交时间")
    private Long createAt;

    private Date createTime;

    private Date updateTime;
}
