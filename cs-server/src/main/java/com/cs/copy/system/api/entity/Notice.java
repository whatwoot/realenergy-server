package com.cs.copy.system.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cs.sp.common.base.BaseDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * <p>
 * 通知公告表
 * </p>
 *
 * @author gpthk
 * @since 2024-09-04
 */
@Getter
@Setter
@TableName("sys_notice")
@Schema(name = "Notice", description = "通知公告表")
public class Notice extends BaseDO {
    private static final long serialVersionUID = 1L;

    @Schema(description = "公告ID")
    @TableId(value = "notice_id", type = IdType.AUTO)
    private Integer noticeId;

    @Schema(description = "公告标题")
    private String noticeTitle;
    @Schema(description = "公告标题英文")
    private String noticeTitleEnUs;
    @Schema(description = "公告标题繁体")
    private String noticeTitleZhTw;
    private String noticeTitleKoKr;
    private String noticeTitleArSa;
    private String noticeTitleViVn;


    @Schema(description = "公告类型（1通知 2公告）")
    private String noticeType;

    @Schema(description = "内容前置")
    private String contentPrefix;
    @Schema(description = "内容前置英文")
    private String contentPrefixEnUs;
    @Schema(description = "内容前置繁体")
    private String contentPrefixZhTw;
    @Schema(description = "内容前置韩语")
    private String contentPrefixKoKr;
    private String contentPrefixArSa;
    private String contentPrefixViVn;

    @Schema(description = "封面")
    private String coverUrl;
    @Schema(description = "封面英文")
    private String coverUrlEnUs;
    @Schema(description = "封面中文")
    private String coverUrlZhTw;
    @Schema(description = "封面韩语")
    private String coverUrlKoKr;
    private String coverUrlArSa;
    private String coverUrlViVn;


    @Schema(description = "公告内容")
    private String noticeContent;
    @Schema(description = "公告内容英文")
    private String noticeContentEnUs;
    @Schema(description = "公告内容繁体")
    private String noticeContentZhTw;
    @Schema(description = "公告内容韩语")
    private String noticeContentKoKr;
    @Schema(description = "公告内容德语")
    private String noticeContentDeDe;
    @Schema(description = "公告内容俄语")
    private String noticeContentRuRu;
    @Schema(description = "公告内容日语")
    private String noticeContentJaJp;
    @Schema(description = "公告内容西语")
    private String noticeContentEsEs;
    private String noticeContentArSa;
    private String noticeContentViVn;

    @Schema(description = "公告状态（0正常 1关闭）")
    private String status;

    @Schema(description = "创建者")
    private String createBy;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "更新者")
    private String updateBy;

    @Schema(description = "更新时间")
    private Date updateTime;

    @Schema(description = "备注")
    private String remark;
}
