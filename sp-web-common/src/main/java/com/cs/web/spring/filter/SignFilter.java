package com.cs.web.spring.filter;


import cn.hutool.extra.spring.SpringUtil;
import com.cs.sp.constant.Constant;
import com.cs.sp.common.Result;
import com.cs.sp.enums.YesNoStrEnum;
import com.cs.sp.util.JsonUtil;
import com.cs.web.spring.config.prop.SpProperties;
import com.cs.web.util.HttpUtil;
import com.cs.web.util.SignUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.SortedMap;


/**
 * 防篡改、防重放攻击过滤器
 */
@Slf4j
public class SignFilter extends OncePerRequestFilter {

    public SignFilter() {
        log.info("SignFilter init");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filter) throws ServletException, IOException {
        boolean cached = request instanceof FullCachingRequestWrapper;
        if (!cached) {
            filter.doFilter(request, response);
            return;
        }

        FullCachingRequestWrapper wrappedRequest = (FullCachingRequestWrapper) request;
        String requestURI = wrappedRequest.getRequestURI();
        String[] whiteLisst = {
                "/swagger-ui.html",
                "/swagger-ui/",
                "/v3/api-docs",
                "/webjars/",
                "/error",
                "/swagger-resources"
        };
        boolean isWhite = Arrays.stream(whiteLisst).parallel().anyMatch(i -> requestURI.startsWith(i));
        if (isWhite) {
            filter.doFilter(request, response);
            return;
        }

        String sign = wrappedRequest.getHeader(com.cs.web.common.Constant.HEADER_SIGN);
        Environment env = SpringUtil.getBean(Environment.class);
        if (!env.acceptsProfiles(Profiles.of(Constant.PROD))
                && Constant.FOR_TEST.equals(sign)) {
            filter.doFilter(request, response);
            return;
        }
        SpProperties spProp = SpringUtil.getBean(SpProperties.class);
        if (StringUtils.hasText(spProp.getSignFilterWhiteUrls())) {
            isWhite = Arrays.stream(spProp.getSignFilterWhiteUrls().split(",")).parallel().anyMatch(i -> requestURI.startsWith(i));
            if (isWhite) {
                filter.doFilter(request, response);
                return;
            }
        }

        String force = wrappedRequest.getHeader(com.cs.web.common.Constant.HEADER_SIGN_FORCE);
        // 如果非强制，并且关了验签
        if (!YesNoStrEnum.YES.eq(force) && !Boolean.TRUE.equals(spProp.getSignFilter())) {
            filter.doFilter(request, response);
            return;
        }

        //验证sign不能为空
        if (StringUtils.isEmpty(sign)) {
            responseFail(response);
            return;
        }

        //验证timestamp是否为空
        String time = wrappedRequest.getHeader(com.cs.web.common.Constant.HEADER_TIMESTAMP);
        if (StringUtils.isEmpty(time)) {
            responseFail(response);
            return;
        }
        /*
         * 重放处理
         * 判断timestamp时间戳与当前时间是否操过60s（过期时间根据业务情况设置）,如果超过了就提示签名过期。
         */
        long timestamp = Long.valueOf(time);
        long now = System.currentTimeMillis() / 1000;
        long n = com.cs.web.common.Constant.SIGN_THRESHOLD;
        if (Math.abs(now - timestamp) > n) {
            responseFail(response);
            return;
        }

        boolean accept = true;
        SortedMap<String, String> paramMap;
        switch (request.getMethod()) {
            case "GET":
                paramMap = HttpUtil.getUrlParams(request);
                accept = SignUtil.verifySign(requestURI, paramMap, sign, timestamp);
                break;
            case "POST":
            case "PUT":
            case "DELETE":
                paramMap = JsonUtil.json2Object(new String(wrappedRequest.getCachedContent(), StandardCharsets.UTF_8), SortedMap.class);
                accept = SignUtil.verifySign(requestURI, paramMap, sign, timestamp);
                break;
            default:
                accept = true;
                break;
        }

        if (accept) {
            filter.doFilter(request, response);
        } else {
            responseFail(response);
        }
    }

    /**
     * 异常返回
     */
    private void responseFail(HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        //抛出异常401 未授权状态码
        response.setStatus(HttpStatus.FORBIDDEN.value());
        PrintWriter out = response.getWriter();
        //统一提示：签名验证失败
        Result fail = Result.fail(com.cs.web.common.Constant.SIGN_403, com.cs.web.common.Constant.SIGN_403_MSG);
        String result = JsonUtil.object2Json(fail);
        out.println(result);
        out.flush();
        out.close();
    }

}
