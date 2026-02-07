package com.cs.web.interceptor;

import com.alibaba.fastjson2.JSONObject;
import com.cs.sp.common.HttpStatus;
import com.cs.sp.common.WebAssert;
import com.cs.web.annotation.LoginRequired;
import com.cs.web.common.Constant;
import com.cs.web.jwt.JwtHelper;
import com.cs.web.jwt.JwtUser;
import com.cs.web.jwt.JwtUserHolder;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Arrays;

import static com.cs.sp.common.WebAssert.throwException;

/**
 * 通过META-INF/ imports注入
 *
 * @author sb
 * @date 2023/5/30 20:19
 */
@Slf4j
public class JwtAuthInterceptor implements HandlerInterceptor {

    private JwtHelper jwtHelper;

    public JwtAuthInterceptor(JwtHelper jwtHelper) {
        this.jwtHelper = jwtHelper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 非controller请求直接过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        String[] whiteList = new String[]{
                "/swagger-ui.html",
                "/swagger-ui/",
                "/v3/api-docs",
                "/webjars/",
                "/error",
                "/swagger-resources",
        };
        final String requestURI = request.getRequestURI();
        boolean isWhite = Arrays.stream(whiteList).anyMatch(i -> requestURI.startsWith(request.getContextPath() + i));
        log.debug("JwtAuthInterceptor {} W:{}", requestURI, isWhite);
        if (isWhite) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();

        String token = getToken(request);
        boolean hasToken = StringUtils.hasText(token);
        Boolean loginRequired = false;
        // 检查是否需要用户权限的注解
        if (method.isAnnotationPresent(LoginRequired.class)) {
            loginRequired = true;
            // 强制登录，则token不能为空
            WebAssert.needLogin(hasToken);
        }
        // 如果不强制登录，并且无token，则直接过
        if (!loginRequired && !hasToken) {
            return true;
        }

        JwtUser jwtUser;
        try {
            Claims claims = jwtHelper.getClaimsFromToken(token);
            jwtUser = JSONObject.parseObject(claims.getSubject(), JwtUser.class);
            jwtUser.setExpired(claims.getExpiration());
            JwtUserHolder.set(jwtUser);
            MDC.put(Constant.REQ_UID, jwtUser.getId().toString());
        } catch (ExpiredJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            String code = e instanceof ExpiredJwtException ? "auth.tokenExpired" : "auth.invalidToken";
            // 有token场景，但是不强制登录，则忽略jwt的异常
            if (loginRequired) {
                throwException(HttpStatus.UNAUTHORIZED, code);
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (JwtUserHolder.get() != null) {
            JwtUserHolder.clear();
        }
        MDC.remove(Constant.REQ_UID);
    }

    private String getToken(HttpServletRequest request) {
        String token = request.getHeader(Constant.HEADER_ACCESS_TOKEN);
        if (token == null) {
            token = request.getParameter(Constant.QUERY_ACCESS_TOKEN);
        }
        return token;
    }
}
