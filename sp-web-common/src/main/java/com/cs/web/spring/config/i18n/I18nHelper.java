package com.cs.web.spring.config.i18n;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

/**
 * @author sb
 * @date 2023/8/28 14:44
 */
public class I18nHelper {

    private MessageSource messageSource;

    public I18nHelper(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMsg(String code, Object... args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }

    public String getMsg(Locale locale, String code, Object... args) {
        return messageSource.getMessage(code, args, locale);
    }

    public String getCnMsg(String code, Object... args) {
        return messageSource.getMessage(code, args, new Locale("zh", "TW"));
    }
}
