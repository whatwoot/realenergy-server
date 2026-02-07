package com.cs.copy.system.api.request;

import com.cs.web.base.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author fiona
 * @date 2024/9/4 17:46
 */
@Data
@Schema(description = "公告列表请求")
public class NoticeListRequest extends BasePageRequest {
    @Schema(description = "公告类型（2公告, 3=新闻，4=快讯）")
    private String noticeType;
}
