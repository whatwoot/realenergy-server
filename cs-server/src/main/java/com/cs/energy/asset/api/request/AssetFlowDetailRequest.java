package com.cs.energy.asset.api.request;

import com.cs.sp.common.base.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author fiona
 * @date 2025/2/26 18:46
 */
@Data
@Schema(description = "资产详情请求")
public class AssetFlowDetailRequest extends BaseRequest {
    @Schema(description = "资产流水id")
    private Long id;
}
