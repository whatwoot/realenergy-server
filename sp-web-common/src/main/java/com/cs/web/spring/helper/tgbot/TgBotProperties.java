package com.cs.web.spring.helper.tgbot;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @authro fun
 * @date 2025/4/7 19:26
 */
@Data
@ConfigurationProperties("tg.bot.config")
public class TgBotProperties {
    private Boolean enable;
    private String api;
}
