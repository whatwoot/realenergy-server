package com.cs.copy.thd.api.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Slf4j
public class SignatureUtil {

    private static final long MAX_TIMESTAMP_DIFF = 5 * 1000; // 5秒内有效

    /**
     * 生成签名
     *
     * @param params    参数，不包含sign和apiSecret
     * @param apiSecret API密钥
     * @return 签名
     */
    public static String generateSignature(Map<String, String> params, String apiSecret) {
        // 按参数名ASCII码从小到大排序
        TreeMap<String, String> sortedParams = new TreeMap<>(params);

        // 拼接成key=value&key=value的形式
        String paramStr = sortedParams.entrySet().stream()
                .filter(entry -> entry.getValue() != null) // 过滤掉值为null的参数
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));

        // 拼接上key=密钥
        String signString = paramStr + "&key=" + apiSecret;

        log.debug("待签名字符串: {}", signString);

        // SHA256签名并转大写
        return sha256(signString).toUpperCase();
    }

    /**
     * 将请求对象转换为参数Map
     */
    public static Map<String, String> buildParamMap(Object request) {
        Map<String, String> result = new HashMap<>();

        // 使用Java反射获取对象的所有字段和值
        Arrays.stream(request.getClass().getDeclaredFields())
                .forEach(field -> {
                    try {
                        field.setAccessible(true);
                        Object value = field.get(request);

                        if (value != null && !field.getName().equals("sign") && !field.getName().equals("apiSecret")) {
                            result.put(field.getName(), value.toString());
                        }
                    } catch (Exception e) {
                        log.error("Failed to get field value", e);
                    }
                });

        // 处理父类字段
        Class<?> superClass = request.getClass().getSuperclass();
        while (superClass != null && !superClass.equals(Object.class)) {
            Arrays.stream(superClass.getDeclaredFields())
                    .forEach(field -> {
                        try {
                            field.setAccessible(true);
                            Object value = field.get(request);

                            if (value != null && !field.getName().equals("sign") && !field.getName().equals("apiSecret")) {
                                result.put(field.getName(), value.toString());
                            }
                        } catch (Exception e) {
                            log.error("Failed to get super field value", e);
                        }
                    });

            superClass = superClass.getSuperclass();
        }

        return result;
    }

    /**
     * 验证时间戳是否在有效期内
     */
    private static boolean verifyTimestamp(String timestampStr) {
        if (!StringUtils.hasText(timestampStr)) {
            return false;
        }

        try {
            long timestamp = Long.parseLong(timestampStr);
            long currentTime = Instant.now().toEpochMilli();

            return Math.abs(currentTime - timestamp) <= MAX_TIMESTAMP_DIFF;
        } catch (NumberFormatException e) {
            log.error("Invalid timestamp format: {}", timestampStr);
            return false;
        }
    }

    /**
     * SHA256加密
     */
    public static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            // 将字节数组转换为十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("SHA-256算法不可用", e);
            throw new RuntimeException("SHA-256算法不可用", e);
        }
    }
}