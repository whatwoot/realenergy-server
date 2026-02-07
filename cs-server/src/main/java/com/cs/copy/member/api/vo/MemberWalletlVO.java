package com.cs.copy.member.api.vo;

import com.cs.sp.common.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author fiona
 * @date 2024/9/29 21:06
 */
@Data
@Schema(description = "用户钱包列表")
public class MemberWalletlVO extends BaseVO {
    @Schema(description = "类型:1=充值地址,2=提现地址")
    private Byte type;

    @Schema(description = "链")
    private String chain;

    @Schema(description = "UID")
    private Long uid;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "钱包地址")
    private String wallet;

    @Schema(description = "备注")
    private String memo;

    @Schema(description = "创建于")
    private Long createAt;
}
