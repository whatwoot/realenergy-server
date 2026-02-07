package com.cs.copy.system.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
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
 * @since 2023-11-13
 */
@Getter
@Setter
@TableName("s_config")
@Schema(name = "Config", description = "$!{table.comment}")
public class Config extends BaseDO {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "配置类型。0=配置类,1=数据类(基本上配好就不会变动的那种),2=页面开关类")
    private Byte type;

    @Schema(description = "配置分类")
    private String category;

    @Schema(description = "配置key")
    private String configKey;

    @Schema(description = "配置名称")
    private String configName;

    @Schema(description = "配置值")
    private String configValue;

    @Schema(description = "配置序号")
    private Integer seq;

    @Schema(description = "配置规则")
    private String rule;

    @Schema(description = "描述")
    private String memo;

    @Schema(description = "1=有效，0=无效")
    private Byte status;

    @Schema(description = "显示。1=有效，0=无效")
    private Byte showed;

    @Schema(description = "权重。从大到小")
    private Integer weight;

    private Date createTime;

    private Date updateTime;

    @Schema(description = "0=未删除，其他=删除")
    @TableLogic(delval = "id")
    private Long deleted;
}
