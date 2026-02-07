package com.cs.web.util;

import com.cs.sp.constant.Constant;
import com.cs.sp.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StreamUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * http 工具类 获取请求中的参数
 */
@Slf4j
public class HttpUtil {
    /**
     * post请求处理：获取 Body 参数，转换为SortedMap
     *
     * @param request
     */
    public static SortedMap<String, String> getBodyParams(final HttpServletRequest request) throws IOException {
        byte[] requestBody = StreamUtils.copyToByteArray(request.getInputStream());
        String body = new String(requestBody);
        return JsonUtil.json2Object(body, SortedMap.class);
    }

    /**
     * get请求处理：将URL请求参数转换成SortedMap
     *
     * @param request
     */
    public static SortedMap<String, String> getUrlParams(HttpServletRequest request) {
        SortedMap<String, String> result = new TreeMap<>();
        Enumeration<String> paramNames = request.getParameterNames();

        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            String paramValue = request.getParameter(paramName);
            result.put(paramName, paramValue);
        }

        return result;
    }

    public static String getSslServerUrl() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attr.getRequest();
        String port = Arrays.asList(80, 443).indexOf(request.getServerPort()) > -1 ? "" : ":" + request.getServerPort();
        return String.format("%s://%s%s%s", Constant.HTTPS, request.getServerName(), port, request.getContextPath());
    }

    public static String getServerUrl() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attr.getRequest();
        String port = Arrays.asList(80, 443).indexOf(request.getServerPort()) > -1 ? "" : ":" + request.getServerPort();
        log.info("ServerUrl {} {} {}", request.getHeader("X-Forwarded-Proto"), request.getScheme(), request.getServerName());
        return String.format("%s://%s%s%s", request.getScheme(), request.getServerName(), port, request.getContextPath());
    }

    public static String getFixSubDomain(String fixSub){
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attr.getRequest();
        String port = Arrays.asList(80, 443).indexOf(request.getServerPort()) > -1 ? "" : ":" + request.getServerPort();
        String rootDomain = fallbackRootDomain(request.getServerName());
        return String.format("%s://%s%s%s", "https", fixSub, rootDomain, port);
    }

    /**
     * 暂时不考虑co.uk,com.cn这种域名
     * @param domain
     * @return
     */
    public static String fallbackRootDomain(String domain){
        String[] parts = domain.split("\\.");
        if (parts.length >= 2) {
            return parts[parts.length - 2] + "." + parts[parts.length - 1];
        }
        return domain;
    }
}
