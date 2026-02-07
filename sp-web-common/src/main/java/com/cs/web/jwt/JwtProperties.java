package com.cs.web.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * jwt配置
 *
 * @author sb
 * @date 2023/5/23 16:09
 */
@Data
@ConfigurationProperties("sp.jwt")
public class JwtProperties {
    /**
     *  默认
     */
    private Boolean enabled = Boolean.TRUE;
    /**
     * 签名密钥
     */
    private String signKey = "aaabbbcccdddeeefffggghhhiiijjjkkklllmmmnnnooopppqqqrrrsssttt";

    /**
     * 过期时间,默认7天
     */
    private Long expireTimeInSecond = 7L * 24 * 3600;

    /**
     * 是否自动注册
     * 暂时加全局的jwt，如果需要自己配置，直接设置为false，并自己实现 WebMvcConfigurer
     */
    private Boolean autoReg = Boolean.TRUE;
}
