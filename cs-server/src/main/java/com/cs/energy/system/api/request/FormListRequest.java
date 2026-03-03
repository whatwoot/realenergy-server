package com.cs.energy.system.api.request;

import com.cs.web.base.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = false)
@Data
@Schema(description = "表单列表请求")
public class FormListRequest extends BasePageRequest {

}
