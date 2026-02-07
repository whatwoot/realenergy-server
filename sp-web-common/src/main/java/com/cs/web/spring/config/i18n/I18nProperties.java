package com.cs.web.spring.config.i18n;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Locale;

/**
 * @author sb
 * @date 2023/5/31 12:45
 */
@Data
@ConfigurationProperties("sp.i18n")
public class I18nProperties {
    private Boolean enable;
    /**
     * 默认3个位置
     */
    private String[] paths = {
            "classpath*:i18n/messages",
            "classpath*:i18n/sp",
            "classpath*:i18n/oms",
            "classpath*:i18n/system",
            "classpath*:i18n/tmpl",
            "classpath*:i18n/thd"
    };
    /**
     * 默认的cookieKey
     */
    private String cookieKeyName = "nexa.LOCALE";

    private Locale locale = Locale.ENGLISH;

    private String resolver;
}
