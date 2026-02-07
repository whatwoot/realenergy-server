package com.cs.web.spring.helper.argon2;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author sb
 * @date 2025/2/20 19:50
 */
@Data
@ConfigurationProperties(prefix = "crypt.argon2")
public class Argon2Properties {
    private Integer outLength;
    private Integer ops;
    private Integer memory;
    private Integer parallel;
}
