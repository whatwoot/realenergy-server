package com.cs.copy.system.api.request;

import com.cs.web.base.BaseCursorRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author fiona
 * @date 2024/9/4 17:46
 */
@Data
@Schema(description = "公告列表请求")
public class NoticeCusorRequest extends BaseCursorRequest {
    @Schema(description = "公告类型（1通知 2公告, 3=新闻，4=快讯）")
    private String noticeType;
    @Schema(description = "公告类型（1通知 2公告, 3=新闻，4=快讯）")
    private String noticeTypes;
    @Schema(description = "起始id(不包含）")
    private Long fromId;
    @Schema(description = "截止id(不包含）")
    private Long toId;
    @Schema(description = "排序，默认逆序。1=时间倒序,0=时间正序")
    private Byte sort;
}
