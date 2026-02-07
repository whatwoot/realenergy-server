package com.cs.copy.member.api.request;

import com.cs.web.base.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author fiona
 * @date 2024/9/29 08:10
 */
@Data
@Schema(description = "邀请列表请求")
public class InviteListRequest extends BasePageRequest {

}
