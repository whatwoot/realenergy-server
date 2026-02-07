package com.cs.web.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author sb
 * @date 2025/2/21 18:18
 */
@Data
@Schema(description = "极验+cf校验联合校验")
public class BaseUnionTestRequest extends BaseCfRequest {
    @JsonProperty("captcha_id")
    private String captchaId;
    @JsonProperty("lot_number")
    private String lotNumber;
    @JsonProperty("captcha_output")
    private String captchaOutput;
    @JsonProperty("pass_token")
    private String passToken;
    @JsonProperty("gen_time")
    private String genTime;
}
