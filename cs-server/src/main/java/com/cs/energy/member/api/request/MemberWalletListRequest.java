package com.cs.energy.member.api.request;

import com.cs.sp.common.base.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author fiona
 * @date 2024/2/3 14:29
 */
@Data
@Schema(description = "用户钱包列表请求")
public class MemberWalletListRequest extends BaseRequest {
    @Schema(description = "链")
    private String chain;
    @Schema(description = "类型。1=充值地址,2=提现地址")
    private Byte type;
}
