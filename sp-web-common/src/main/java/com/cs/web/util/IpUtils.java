package com.cs.web.util;

import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 获取用户IP工具类
 * 增加内网IP检查
 *
 * @author sb
 */
public class IpUtils {

    private static final String SPLIT = ",";
    private static final String UNKNOWN = "unknown";
    private static final String LOCALHOST_IPV4 = "127.0.0.1";
    private static final String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";

    /**
     * 获取客户端真实IP
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = getFirstValidIp(
                request.getHeader("x-forwarded-for"),
                request.getHeader("Proxy-Client-IP"),
                request.getHeader("WL-Proxy-Client-IP")
        );

        if (!isValidIp(ip)) {
            ip = request.getRemoteAddr();
            if (LOCALHOST_IPV6.equals(ip)) {
                ip = LOCALHOST_IPV4;
            }
        }

        // 处理多级代理的IP序列
        if (ip != null && ip.contains(SPLIT)) {
            String[] ips = ip.split(SPLIT);
            for (String candidate : ips) {
                if (isValidIp(candidate)) {
                    if (!isInternalIp(candidate)) {
                        return candidate.trim();
                    }
                }
            }
            // 如果都是内网IP，返回第一个有效的
            for (String candidate : ips) {
                if (isValidIp(candidate)) {
                    return candidate.trim();
                }
            }
        }

        return ip != null ? ip.trim() : "";
    }

    /**
     * 检查是否为内网IP
     */
    public static boolean isInternalIp(String ip) {
        try {
            InetAddress address = InetAddress.getByName(ip);
            return address.isSiteLocalAddress() ||
                    address.isLoopbackAddress() ||
                    isPrivateRange(address.getAddress());
        } catch (UnknownHostException e) {
            return false;
        }
    }

    /**
     * 检查IP是否在私有网络范围内
     */
    private static boolean isPrivateRange(byte[] ip) {
        if (ip.length != 4) return false;

        // 10.0.0.0/8
        if (ip[0] == 10) return true;

        // 172.16.0.0/12
        if (ip[0] == (byte)172 && ip[1] >= 16 && ip[1] <= 31) return true;

        // 192.168.0.0/16
        if (ip[0] == (byte)192 && ip[1] == (byte)168) return true;

        return false;
    }

    /**
     * 从多个IP候选值中获取第一个有效IP
     */
    private static String getFirstValidIp(String... ips) {
        for (String ip : ips) {
            if (isValidIp(ip)) {
                return ip;
            }
        }
        return null;
    }

    /**
     * 验证IP有效性
     */
    private static boolean isValidIp(String ip) {
        return StringUtils.hasText(ip) &&
                !UNKNOWN.equalsIgnoreCase(ip) &&
                !LOCALHOST_IPV4.equals(ip) &&
                !LOCALHOST_IPV6.equals(ip);
    }
}