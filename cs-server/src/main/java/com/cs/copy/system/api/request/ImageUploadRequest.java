package com.cs.copy.system.api.request;

import com.cs.sp.common.base.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author fiona
 * @date 2025/2/25 18:08
 */
@Data
@Schema(description = "base64图片上传")
public class ImageUploadRequest extends BaseRequest {
    @Schema(description = "业务代码。image.avatar=头像上传")
    @NotBlank(message = "chk.common.required")
    @NotNull(message = "chk.common.required")
    private String serviceName;
    @Schema(description = "文件")
    @NotBlank(message = "chk.common.required")
    @NotNull(message = "chk.common.required")
    private String f;
}
