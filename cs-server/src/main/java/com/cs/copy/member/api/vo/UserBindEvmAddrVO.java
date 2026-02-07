package com.cs.copy.member.api.vo;

import com.cs.sp.common.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author fiona
 * @date 2024/9/29 21:06
 */
@Data
@Schema(description = "用户绑定信息")
public class UserBindEvmAddrVO extends BaseVO {
    private Long id;
    @Schema(description = "钱包地址")
    private String evmAddr;
}
