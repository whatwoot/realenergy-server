package com.cs.energy.system.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
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
 * @since 2025-02-18
 */
@Getter
@Setter
@TableName("s_banner")
@Schema(name = "Banner", description = "")
public class Banner implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "位置")
    private String pos;

    @Schema(description = "类型。")
    private Byte type;

    @Schema(description = "标题")
    private String title;
    private String titleZhTw;
    private String titleEnUs;
    private String titleKoKr;

    @Schema(description = "内容")
    private String content;
    private String contentZhTw;
    private String contentEnUs;
    private String contentKoKr;

    @Schema(description = "缩略图")
    private String thumbUrl;

    @Schema(description = "主图")
    private String imageUrl;
    private String imageUrlZhTw;
    private String imageUrlKoKr;
    private String imageUrlEnUs;

    private String jumpUrl;

    @Schema(description = "状态。1=有效,0=无效")
    private Byte status;

    @Schema(description = "生效于")
    private Long validAt;

    @Schema(description = "失效于")
    private Long invalidAt;

    @Schema(description = "开始于")
    private Long startAt;

    @Schema(description = "结束于")
    private Long endAt;

    @Schema(description = "权重")
    private Integer weight;

    @Schema(description = "备注")
    private String memo;

    private Date createTime;

    private Date updateTime;

    @TableLogic(delval = "id")
    private Integer deleted;
}
