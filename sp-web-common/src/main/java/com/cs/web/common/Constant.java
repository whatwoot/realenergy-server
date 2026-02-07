package com.cs.web.common;

/**
 * @author sb
 * @date 2023/5/30 21:27
 */
public interface Constant {
    String HEADER_ACCESS_TOKEN = "Authorization";
    String QUERY_ACCESS_TOKEN = "access_token";
    String HEADER_USER_AGENT = "User-Agent";
    String HEADER_REFERER = "Referer";
    String HEADER_ACCEPT = "accept";
    String EVENT_STREAM = "text/event-stream";
    String[] JSON_WHITELIST_STR = {"org.springframework", "com.g"};
    String HEADER_REQ_ID = "X-Request-ID";
    String REQ_IP = "RIP";
    String REQ_ID = "RID";
    String REQ_SID = "RSID";
    String REQ_UID = "RUID";
    String METHOD_POST = "POST";
    Integer REQ_CACHE_LIMIT = 8 * 1024 * 1024;
    String HEADER_SIGN = "sign";
    String HEADER_SIGN_FORCE = "sign-force";
    String HEADER_TIMESTAMP = "timestamp";
    Long SIGN_THRESHOLD = 60L;
    String SIGN_403 = "sp.security.403";
    String SIGN_403_MSG = "Please wait";
    String RATELIMIT_LOCK = "__ratelimit:";
}
