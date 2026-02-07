package com.cs.web.spring.filter;

import com.cs.web.common.Constant;
import com.cs.web.util.IpUtils;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @authro fun
 * @date 2025/5/24 03:38
 */
public class CachingRequestFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 生成唯一请求ID（如果前端未提供）
        String requestId = request.getHeader(Constant.HEADER_REQ_ID);
        if (requestId == null) {
            requestId = genRequestId(0);
        }
        MDC.put(Constant.REQ_ID, requestId);
        MDC.put(Constant.REQ_SID, request.getSession().getId());
        MDC.put(Constant.REQ_IP, IpUtils.getIpAddr(request));
        try {
            // 检查是否是文件上传请求
            if (isMultipartRequest(request)) {
                // 文件上传请求不包装
                filterChain.doFilter(request, response);
            } else {
                // 普通请求使用缓存包装器
                FullCachingRequestWrapper wrappedRequest = new FullCachingRequestWrapper(request);
                // 触发缓存
                wrappedRequest.getInputStream();
                filterChain.doFilter(wrappedRequest, response);
            }
        } finally {
            MDC.remove(Constant.REQ_ID);
            MDC.remove(Constant.REQ_SID);
            MDC.remove(Constant.REQ_IP);
        }
    }

    private boolean isMultipartRequest(HttpServletRequest request) {
        String contentType = request.getContentType();
        return contentType != null && contentType.startsWith("multipart/") && Constant.METHOD_POST.equalsIgnoreCase(request.getMethod());
    }

    /**
     * 0 单机来表示用这个更快
     * 1 微服务建议用uuid
     */
    private String genRequestId(int type) {
        if (type == 0) {
            long timestamp = System.currentTimeMillis();
            int random = 10000 + ThreadLocalRandom.current().nextInt(10000);
            return timestamp + "-" + random;
        } else {
            return UUID.randomUUID().toString();
        }
    }
}
