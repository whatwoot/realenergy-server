package com.cs.energy.system.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@TableName("s_form")
@Schema(name = "FORM", description = "")
public class Form implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Long uid;

    @Schema(description = "类型")
    private Byte type;

    @Schema(description = "实体类")
    private String entityClass;

    @Schema(description = "表单值")
    private String json;

    @Schema(description = "提交时间")
    private Date createTime;

    @TableLogic(delval = "id")
    private Integer deleted;
}
