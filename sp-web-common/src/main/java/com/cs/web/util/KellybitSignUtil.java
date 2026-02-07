package com.cs.web.util;

import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Map;
import java.util.StringJoiner;

/**
 * @author sb
 * @date 2023/7/26 11:39
 */
@Slf4j
public class KellybitSignUtil {

    //    private static String API_HOST = "api.kellybit.com";
    private static String API_HOST = "8.222.245.96";
    private static final String SIGNATURE_METHOD = "HmacSHA256";
    private static final String SIGNATURE_VERSION = "2";

    private static final ZoneId ZONE_GMT = ZoneId.of("Z");

    private static final DateTimeFormatter DT_FORMAT = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss");

    public static void setApiHost(String apiHost) {
        API_HOST = apiHost;
    }

    public static String getTimestamp() {
        String s = Instant.now().atZone(ZONE_GMT).format(DT_FORMAT);
//        return encode(s);
        return s;
    }

    public static Map<String, Object> prepareMap(Map map, String apiKey, String timeStamp, String sign) {
        Map newMap = map;
        newMap.put("accessKeyId", apiKey);
        newMap.put("signatureMethod", SIGNATURE_METHOD);
        newMap.put("signatureVersion", SIGNATURE_VERSION);
        newMap.put("timestamp", encode(timeStamp));
        newMap.put("signature", sign);
        return newMap;


    }

    public static String sign(String method, String path, Map map, String timeStamp, String apiKey, String secretKey) {
        StringBuilder sb = new StringBuilder(1024);
        // GET
        sb.append(method.toUpperCase()).append('\n')
                // Host
                .append(API_HOST.toLowerCase()).append('\n')
                // path
                .append(path).append('\n');


        StringJoiner joiner = new StringJoiner("&");
        joiner.add("accessKeyId=" + apiKey)
                .add("signatureMethod=" + SIGNATURE_METHOD)
                .add("signatureVersion=" + SIGNATURE_VERSION)
                .add("timestamp=" + encode(timeStamp));


        //拼接 遍历map
        Iterator<Map.Entry<String, String>> entries = map.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, String> entry = entries.next();
            joiner.add(entry.getKey() + "=" + entry.getValue());
        }
        String sign = HmacSHA256Signer.sign(sb.toString() + joiner.toString(), secretKey);
        log.info("sb={},joiner={},sign={}", sb.toString(), joiner.toString(), sign);
        return sign;
    }

    private static String encode(String code) {
        try {
            return URLEncoder.encode(code, "UTF-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
