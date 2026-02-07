package com.cs.web.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * @author sb
 * @date 2022/3/31 04:22
 */
public class BaseController {

    @Autowired
    private MessageSource ms;

    protected String getMsg(String code, Object... args) {
        return ms.getMessage(code, args, LocaleContextHolder.getLocale());
    }

}
