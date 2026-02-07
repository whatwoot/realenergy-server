package com.cs.web.spring.config;

import com.cs.web.interceptor.AutoRegInterceptor;
import com.cs.web.spring.config.prop.CorsProperties;
import com.cs.web.spring.config.prop.SpProperties;
import com.cs.web.spring.filter.CachingRequestFilter;
import com.cs.web.spring.filter.SignFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * jackson配置请使用JacksonConfig
 *
 * @author sb
 * @date 2023/5/5 09:22
 */
@Configuration(proxyBeanMethods = false)
@Slf4j
@EnableConfigurationProperties({CorsProperties.class, SpProperties.class})
@ConditionalOnProperty(name = "sp.mvc.enable", havingValue = "true", matchIfMissing = true)
public class MvcConfig implements WebMvcConfigurer {

    private List<AutoRegInterceptor> authRegInterceptors;

    public MvcConfig(List<AutoRegInterceptor> authRegInterceptors) {
        this.authRegInterceptors = authRegInterceptors;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        InterceptorRegistration reg;
        for (AutoRegInterceptor inteceptorReg : authRegInterceptors) {
            log.info("autoReg {}", inteceptorReg.getName());
            reg = registry.addInterceptor(inteceptorReg.getInterceptor())
                    .order(inteceptorReg.getOrder());
            if (inteceptorReg.getPatterns() != null) {
                reg.addPathPatterns(inteceptorReg.getPatterns());
            }
            if (inteceptorReg.getExcludePatterns() != null) {
                reg.excludePathPatterns(inteceptorReg.getExcludePatterns());
            }
        }
    }

    @Bean
    public FilterRegistrationBean<CachingRequestFilter> cacheRequestFilter() {
        FilterRegistrationBean<CachingRequestFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new CachingRequestFilter());
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE); // 最高优先级
        return bean;
    }

    @Bean
    @ConditionalOnProperty(name = "sp.config.signFilter", havingValue = "true")
    public FilterRegistrationBean<SignFilter> signFilter() {
        FilterRegistrationBean<SignFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new SignFilter());
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE + 100); // 最高优先级
        return bean;
    }


    /**
     * 响应添加etag，用于做get请求的304
     *
     * @return
     */
    @Bean
    public ShallowEtagHeaderFilter shallowEtagHeaderFilter() {
        log.info("etag init");
        return new ShallowEtagHeaderFilter();
    }

    /**
     * cors注册
     *
     * @param corsProperties
     * @return
     */
    @Bean
    @ConditionalOnProperty(name = "sp.cors.origins", matchIfMissing = true)
    public FilterRegistrationBean corsFilter(CorsProperties corsProperties) {
        log.info("corsFilter init {}: {}", corsProperties.getPath(), corsProperties.getOrigins());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowPrivateNetwork(true);
        String[] split = corsProperties.getOrigins().split(",");
        for (String origin : split) {
            config.addAllowedOriginPattern(origin);
        }
        config.addAllowedHeader(CorsConfiguration.ALL);
        config.addAllowedMethod(CorsConfiguration.ALL);

        // 这里可以设置针对路径的，不同源策略
        source.registerCorsConfiguration(corsProperties.getPath(), config);

        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }
}
