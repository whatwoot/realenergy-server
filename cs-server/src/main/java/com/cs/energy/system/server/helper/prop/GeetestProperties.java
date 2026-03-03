package com.cs.energy.system.server.helper.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @authro fun
 * @date 2025/5/29 20:52
 */
@Data
@ConfigurationProperties("geetest.config")
public class GeetestProperties {
    // 默认值，可通过配置覆盖
    private String api = "https://gcaptcha4.geetest.com";
    private String captchaId;
    private String captchaKey;
}
