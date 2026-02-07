package com.cs.web.spring.helper;

import com.cs.web.spring.helper.tgbot.TgBotHelper;
import com.cs.web.spring.helper.tgbot.TgBotProperties;
import com.cs.web.spring.helper.tgbot.event.TgBotListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @authro fun
 * @date 2025/4/7 19:25
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({TgBotProperties.class})
@ConditionalOnProperty(prefix = "tg.bot.config", name = "enable")
public class TgBotConfig {

    @Bean
    @ConditionalOnMissingBean
    public TgBotHelper tgBotHelper(TgBotProperties tgBotProperties) {
        log.info("tgBotHelper init");
        return new TgBotHelper(tgBotProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean({TgBotHelper.class})
    public TgBotListener tgBotListener(TgBotHelper tgBotHelper) {
        log.info("TgBotListener init");
        return new TgBotListener(tgBotHelper);
    }
}
