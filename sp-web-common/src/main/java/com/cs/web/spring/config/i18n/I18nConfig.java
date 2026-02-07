package com.cs.web.spring.config.i18n;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

/**
 * @author sb
 * @date 2023/5/5 09:22
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(value = WebMvcAutoConfiguration.class)
@EnableConfigurationProperties({I18nProperties.class})
@ConditionalOnProperty(name = "sp.i18n.enable", havingValue = "true", matchIfMissing = true)
@Slf4j
public class I18nConfig implements WebMvcConfigurer {

    @Bean
    public MessageSource messageSource(I18nProperties i18nProperties) {
        log.info("i18n messageSource init");
        ReloadableResourceBundleMessageSource messageSource = new SmReloadableResourceBundleMessageSource();
        messageSource.setBasenames(i18nProperties.getPaths());
        messageSource.setUseCodeAsDefaultMessage(true);
        messageSource.setFallbackToSystemLocale(false);
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setDefaultLocale(i18nProperties.getLocale());
        return messageSource;
    }

    @Bean
    @ConditionalOnBean(MessageSource.class)
    public I18nHelper i18nHelper(MessageSource messageSource) {
        log.info("I18nHelper init");
        return new I18nHelper(messageSource);
    }

    @Bean
    @ConditionalOnProperty(name = "sp.i18n.resolver", havingValue = "cookie")
    public LocaleResolver cookieLocaleResolver(I18nProperties i18nProperties) {
        log.info("i18n cookieLocaleResolver init");
        CookieLocaleResolver localeResolver = new CookieLocaleResolver();
        localeResolver.setDefaultLocale(i18nProperties.getLocale());
        localeResolver.setCookieName(i18nProperties.getCookieKeyName());
        return localeResolver;
    }

    @Bean
    @ConditionalOnProperty(name = "sp.i18n.resolver", havingValue = "fix")
    public LocaleResolver localeResolver(I18nProperties i18nProperties) {
        log.info("i18n fixedLocaleResolver init {}", i18nProperties.getLocale());
        return new FixedLocaleResolver(i18nProperties.getLocale());
    }

    @Bean
    @ConditionalOnProperty(name = "sp.i18n.canChange", havingValue = "true")
    public LocaleChangeInterceptor localeChangeInterceptor() {
        log.info("localeChangeInterceptor init");
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setIgnoreInvalidLocale(true);
        lci.setParamName("lang");
        return lci;
    }
}
