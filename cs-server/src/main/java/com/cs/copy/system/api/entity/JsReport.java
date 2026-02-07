package com.cs.copy.system.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cs.sp.common.base.BaseDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author gpthk
 * @since 2024-03-27
 */
@Getter
@Setter
@TableName("s_js_report")
@Schema(name = "JsReport", description = "$!{table.comment}")
public class JsReport extends BaseDO {

    private static final long serialVersionUID = 2933278952673220481L;
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long uid;

    private String ip;
    private String addr;

    private String url;

    private String platform;

    private String device;
    private String browser;
    private String engine;
    private String ua;
    private String screen;
    private String type;
    private String msg;
    private String fileUrl;
    private String fileName;
    private Integer lineNo;
    private String detail;
    private Date createTime;
}
