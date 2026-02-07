package com.cs.web.spring.web.prop;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author sb
 * @date 2024/5/19 17:39
 */
@Getter
@Setter
@ConfigurationProperties("sp.advice")
public class AdviceProperties {
    private Resp resp;
    private Excep ex;

    @Getter
    @Setter
    public static class Resp{
        private String[] basePackages;
    }

    @Getter
    @Setter
    public static class Excep{
        private String[] basePackages;
    }
}
