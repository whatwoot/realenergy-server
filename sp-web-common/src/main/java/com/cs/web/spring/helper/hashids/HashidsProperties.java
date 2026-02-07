package com.cs.web.spring.helper.hashids;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author sb
 * @date 2024/9/28 17:55
 */
@Data
@ConfigurationProperties(prefix = "crypt.hashids")
public class HashidsProperties {
    private String first;
    private String salt;
    private Integer minLength;
    private String alphabet;
}
