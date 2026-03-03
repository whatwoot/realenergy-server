package com.cs.energy.asset.api.dto;

import com.cs.sp.common.base.BaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @authro fun
 * @date 2025/11/10 02:03
 */
@Data
public class WithdrawParamDTO extends BaseDTO {
    @Schema(description = "类型。1=微信,2=支付宝,4=聚合码")
    private Byte type;

    @Schema(description = "账号")
    private String account;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "银行")
    private String bank;

    @Schema(description = "支行")
    private String branch;

    @Schema(description = "备注")
    private String memo;

    @Schema(description = "二维码地址")
    private String codeUrl;

    @Schema(description = "二维码内容")
    private String code;
}
