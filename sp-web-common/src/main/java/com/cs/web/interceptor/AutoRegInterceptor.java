package com.cs.web.interceptor;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.ArrayList;
import java.util.List;

/**
 * 自动注册的拦截器
 *
 * @author sb
 * @date 2023/8/15 22:14
 */
@Getter
@Setter
@NoArgsConstructor
public class AutoRegInterceptor<T extends HandlerInterceptor> {

    private String name;
    private T interceptor;
    private Integer order = 0;
    private List<String> patterns;
    private List<String> excludePatterns;

    public AutoRegInterceptor(T interceptor) {
        this.interceptor = interceptor;
    }

    public void addPatterns(String... paths) {
        if (patterns != null) {
            patterns = new ArrayList<>();
        }
        for (String path : paths) {
            patterns.add(path);
        }
    }

    public void addExcludePatterns(String... excludePaths) {
        if (excludePatterns != null) {
            excludePatterns = new ArrayList<>();
        }
        for (String path : excludePaths) {
            excludePatterns.add(path);
        }
    }

    public String getName() {
        return StringUtils.hasLength(name) ? name : interceptor.getClass().getSimpleName();
    }
}
