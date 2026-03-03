package com.cs.energy.thd.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author gpthk
 * @since 2025-03-21
 */
@Getter
@Setter
@TableName("z_pay_flow")
@Schema(name = "PayFlow", description = "")
public class PayFlow implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "场景")
    private String scene;

    @Schema(description = "关联id")
    private Long relateId;

    @Schema(description = "供应商")
    private String provider;

    @Schema(description = "用户id")
    private Long uid;

    @Schema(description = "金额")
    private BigDecimal amount;

    @Schema(description = "数量")
    private BigDecimal quantity;

    @Schema(description = "支付方式")
    private String payMode;

    @Schema(description = "支付渠道")
    private String payChannel;

    @Schema(description = "状态。0=待请求,1=已成功,2=请求失败")
    private Byte status;

    @Schema(description = "0=未付,1=已付,2=付失败")
    private Byte payStatus;

    @Schema(description = "请求参数")
    private String extInfo;

    @Schema(description = "请求响应")
    private String resp;

    @Schema(description = "创建时间")
    private Long createAt;

    @Schema(description = "成功通知")
    private Long notifyAt;

    @Schema(description = "失败回调")
    private Long fallbackAt;

    private Date createTime;

    private Date updateTime;
}
