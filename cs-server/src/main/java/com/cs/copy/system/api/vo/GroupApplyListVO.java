package com.cs.copy.system.api.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.cs.copy.system.api.request.GroupApplyRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "团队申请列表")
public class GroupApplyListVO extends GroupApplyRequest {

    @Schema(description = "公告ID")
    private Integer id;

    @JsonIgnore
    @Schema(description = "实体类")
    private String entityClass;

    @JsonIgnore
    @Schema(description = "表单值")
    private String json;

    @Schema(description = "提交时间")
    private Date createTime;
}
