package com.cs.oksdk.config.prop;

import lombok.Data;

/**
 * @authro fun
 * @date 2025/9/7 04:12
 */
@Data
public class OkxProperties {
    private Boolean testnet;
    private String apikey;
    private String secret;
    private String passphrase;
}
