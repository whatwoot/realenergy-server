package com.cs.copy.system.api.request;

import com.cs.sp.common.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "团队申请")
public class GroupApplyRequest extends BaseVO {

    @Schema(description = "你所代表的社区")
    @NotBlank(message = "chk.common.required")
    @NotNull(message = "chk.common.required")
    private String group;

    @Schema(description = "联系方式")
    @NotBlank(message = "chk.common.required")
    @NotNull(message = "chk.common.required")
    private String contract;

    @Schema(description = "需求描述")
    private String description;

    @Schema(description = "人数规模")
    private String headcount;

    @Schema(description = "是否了解")
    private Byte understand;

    @Schema(description = "trc收款地址")
    private String trcAddress;

    @Schema(description = "图片文件")
    @Size(max = 10, message = "chk.apply.sizeInvalid")
    private List<String> photoUrlList;
}
