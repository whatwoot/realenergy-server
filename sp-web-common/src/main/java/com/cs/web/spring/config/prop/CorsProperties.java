package com.cs.web.spring.config.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author sb
 * @date 2024/5/3 02:45
 */

@Data
@ConfigurationProperties("sp.cors")
public class CorsProperties {
    private String path = "/**";
    /**
     * 支持用,配置多个带通配符的域名
     */
    private String origins = "*";
}
