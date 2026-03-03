package com.cs.energy.asset.api.request;

import com.cs.sp.common.base.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @authro fun
 * @date 2026/1/2 14:30
 */
@Data
@Schema(description = "资产明细")
public class AssetListRequest extends BaseRequest {
    @Schema(description = "代币")
    private String symbol;
    @Schema(description = "账户，默认为0。0=默认账号，1=跟单保证金账户")
    private Byte type;
}
