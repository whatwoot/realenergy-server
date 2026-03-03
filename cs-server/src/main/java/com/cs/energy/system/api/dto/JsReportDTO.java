package com.cs.energy.system.api.dto;

import com.cs.sp.common.base.BaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 *
 * </p>
 *
 * @author gpthk
 * @since 2024-03-27
 */
@Data
@Schema(description = "上报接口")
public class JsReportDTO extends BaseDTO {

    @Schema(description = "uid")
    private Long uid;

    @Schema(description = "钱包地址")
    private String addr;

    @Schema(description = "发生的url")
    private String url;

    @Schema(description = "操作系统，eg: windows, android, ios")
    private String platform;

    @Schema(description = "设备")
    private String device;
    @Schema(description = "浏览器")
    private String browser;
    @Schema(description = "浏览器引擎")
    private String engine;

    @Schema(description = "userAgent")
    private String ua;

    @Schema(description = "屏幕,eg: 1440*900")
    private String screen;

    @Schema(description = "错误类型")
    private String type;

    @Schema(description = "错误描述")
    private String msg;

    @Schema(description = "发生文件路径")
    private String fileUrl;

    @Schema(description = "文件名")
    private String fileName;

    @Schema(description = "行号")
    private Integer lineNo;

    @Schema(description = "错误信息堆栈")
    private String detail;
}
