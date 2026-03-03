package com.cs.energy.system.server.helper.prop;

import com.alibaba.fastjson2.annotation.JSONField;
import com.cs.sp.common.base.BaseRequest;
import lombok.Data;

/**
 * @authro fun
 * @date 2025/5/29 21:02
 */
@Data
public class GeetestRequest extends BaseRequest {
    @JSONField(name = "captcha_id")
    private String captchaId;
    @JSONField(name = "lot_number")
    private String lotNumber;
    @JSONField(name = "captcha_output")
    private String captchaOutput;
    @JSONField(name = "pass_token")
    private String passToken;
    @JSONField(name = "gen_time")
    private String genTime;
    @JSONField(name = "sign_token")
    private String signToken;
}
