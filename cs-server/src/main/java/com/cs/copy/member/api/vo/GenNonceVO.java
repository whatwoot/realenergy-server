package com.cs.copy.member.api.vo;

import com.cs.sp.common.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fiona
 * @date 2024/9/29 21:06
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "用户绑定tg信息")
public class GenNonceVO extends BaseVO {
    @Schema(description = "生成Nonce")
    private String nonce;
}
