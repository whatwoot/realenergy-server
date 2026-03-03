package com.cs.energy.system.api.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.cs.energy.system.api.serializer.LangContentSerializer;
import com.cs.energy.system.api.serializer.LangSerializer;
import com.cs.sp.common.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @author fiona
 * @date 2024/9/4 17:48
 */
@Data
@Schema(description = "公告列表结果")
public class NoticeListVO extends BaseVO {

    @Schema(description = "公告ID")
    private Integer noticeId;
    private Integer id;

    @Schema(description = "公告标题")
    @JsonSerialize(using = LangSerializer.class, nullsUsing = LangSerializer.class)
    private String noticeTitle;

    @JsonIgnore
    private String noticeTitleEnUs;
    @JsonIgnore
    private String noticeTitleZhTw;
    @JsonIgnore
    private String noticeTitleKoKr;
    @JsonIgnore
    private String noticeTitleArSa;
    @JsonIgnore
    private String noticeTitleViVn;

    @Schema(description = "封面")
    @JsonSerialize(using = LangSerializer.class, nullsUsing = LangSerializer.class)
    private String coverUrl;

    @JsonIgnore
    private String coverUrlEnUs;
    @JsonIgnore
    private String coverUrlZhTw;
    @JsonIgnore
    private String coverUrlKoKr;
    @JsonIgnore
    private String coverUrlArSa;
    @JsonIgnore
    private String coverUrlViVn;

    @Schema(description = "内容-前缀")
    @JsonSerialize(using = LangSerializer.class, nullsUsing = LangSerializer.class)
    private String contentPrefix;
    @JsonIgnore
    private String contentPrefixEnUs;
    @JsonIgnore
    private String contentPrefixZhTw;
    @JsonIgnore
    private String contentPrefixKoKr;
    @JsonIgnore
    private String contentPrefixArSa;
    @JsonIgnore
    private String contentPrefixViVn;

    @Schema(description = "内容")
    @JsonSerialize(using = LangContentSerializer.class, nullsUsing = LangContentSerializer.class)
    private String noticeContent;
    @JsonIgnore
    private String noticeContentEnUs;
    @JsonIgnore
    private String noticeContentZhTw;
    @JsonIgnore
    private String noticeContentKoKr;
    @JsonIgnore
    private String noticeContentArSa;
    @JsonIgnore
    private String noticeContentViVn;

    @Schema(description = "公告类型（1通知 2公告）")
    private String noticeType;

    @Schema(description = "来源")
    private String createBy;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "更新时间")
    private Date updateTime;

    @Schema(description = "备注")
    private String remark;


    public Integer getId() {
        return noticeId;
    }
}
