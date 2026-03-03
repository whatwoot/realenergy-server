package com.cs.energy.system.api.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.cs.energy.system.api.serializer.LangSerializer;
import com.cs.sp.common.base.BaseVO;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author fiona
 * @date 2024/9/4 17:48
 */
@Data
@Schema(description = "公告列表结果")
public class BannerVO extends BaseVO {

    @Schema(description = "位置。index.banner=首页banner位")
    private String pos;

    @Schema(description = "类型")
    private Byte type;

    @Schema(description = "标题")
    @JsonSerialize(using = LangSerializer.class, nullsUsing = LangSerializer.class)
    private String title;
    @Hidden
    @JsonIgnore
    private String titleZhTw;
    @Hidden
    @JsonIgnore
    private String titleEnUs;
    @Hidden
    @JsonIgnore
    private String titleKoKr;
    @Hidden
    @JsonIgnore
    private String titleViVn;
    @Hidden
    @JsonIgnore
    private String titleArSa;

    @Schema(description = "内容")
    @JsonSerialize(using = LangSerializer.class, nullsUsing = LangSerializer.class)
    private String content;
    @Hidden
    @JsonIgnore
    private String contentZhTw;
    @Hidden
    @JsonIgnore
    private String contentEnUs;
    @Hidden
    @JsonIgnore
    private String contentKoKr;
    @Hidden
    @JsonIgnore
    private String contentViVn;
    @Hidden
    @JsonIgnore
    private String contentArSa;

    @Schema(description = "跳转地址")
    private String jumpUrl;

    @Schema(description = "缩略图")
    private String thumbUrl;

    @Schema(description = "主图")
    @JsonSerialize(using = LangSerializer.class, nullsUsing = LangSerializer.class)
    private String imageUrl;
    @Hidden
    @JsonIgnore
    private String imageUrlZhTw;
    @Hidden
    @JsonIgnore
    private String imageUrlKoKr;
    @Hidden
    @JsonIgnore
    private String imageUrlEnUs;
    @Hidden
    @JsonIgnore
    private String imageUrlViVn;
    @Hidden
    @JsonIgnore
    private String imageUrlArSa;


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

}
