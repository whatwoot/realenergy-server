package com.cs.copy.asset.api.request;

import com.cs.web.base.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author fiona
 * @date 2024/9/29 07:38
 */
@Data
@Schema(description = "资产明细")
public class AssetFlowListRequest extends BasePageRequest {

    @Schema(description = "账户，默认为0。0=默认账号，1=溢出账户")
    private Byte type;
    @Schema(description = "场景(场景按逗号拼接)")
    private String scenes;
    @Schema(description = "单个场景（优先级高于多场景）")
    private String scene;
    @Schema(description = "币种")
    private String symbol;

    @Schema(description = "是否对用户显示，非必填，默认取1。1=显示,0=不显示")
    private Byte showed;
}
