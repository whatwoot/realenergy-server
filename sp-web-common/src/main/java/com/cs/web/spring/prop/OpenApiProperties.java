package com.cs.web.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author sb
 * @date 2023/9/21 22:23
 */
@Data
@ConfigurationProperties("sp.openapi")
public class OpenApiProperties {
    private String title;
    private String desc;
    private String version;
}
