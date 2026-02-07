package com.cs.web.spring.helper.aeshelper;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author sb
 * @date 2024/2/26 03:54
 */


@Data
@ConfigurationProperties(prefix = "crypt.aes")
public class AesProperties {
    private String secretKey;
    private String charset;
}
