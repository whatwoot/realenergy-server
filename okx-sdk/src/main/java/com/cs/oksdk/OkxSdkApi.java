package com.cs.oksdk;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.cs.oksdk.config.OkxHttpHelper;
import com.cs.oksdk.config.prop.OkxProperties;
import com.cs.oksdk.reponse.BalanceRes;
import com.cs.oksdk.request.BalanceRequest;
import com.cs.oksdk.request.ClosePositionRequest;
import com.cs.oksdk.request.InstrumentsRequest;
import com.cs.oksdk.request.OrderRequest;
import com.cs.sp.constant.Constant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * @authro fun
 * @date 2025/11/25 21:06
 */
@Slf4j
public class OkxSdkApi {
    private OkxProperties okxProperties;
    private HmacUtils hmacUtils;
    private OkxHttpHelper httpHelper;

    private static final String HEADER_OK_ACCESS_KEY = "OK-ACCESS-KEY";
    private static final String HEADER_OK_ACCESS_SIGN = "OK-ACCESS-SIGN";
    private static final String HEADER_OK_ACCESS_TIMESTAMP = "OK-ACCESS-TIMESTAMP";
    private static final String HEADER_OK_ACCESS_PASSPHRASE = "OK-ACCESS-PASSPHRASE";
    private static final String HEADER_X_SIMULATED_TRADING = "x-simulated-trading";
    public static final String ONE_STR = "1";
    public static final String BASE_URL = "https://www.okx.com";
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String QUERY_START = "?";

    public OkxSdkApi(OkxProperties okxProperties) {
        this.okxProperties = okxProperties;
        this.hmacUtils = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, okxProperties.getSecret());
        this.httpHelper = new OkxHttpHelper();
    }

    public OkxSdkApi(OkxProperties okxProperties, OkxHttpHelper httpHelper) {
        this.okxProperties = okxProperties;
        this.hmacUtils = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, okxProperties.getSecret());
        this.httpHelper = httpHelper;
    }

    public String getStatus() {
        return http(GET, OkxV5.API_STATUS, null);
    }

    public BalanceRes balance(BalanceRequest req) {
        String s = http(GET, OkxV5.API_BALANCE, req);
        return JSONObject.parseObject(s, BalanceRes.class);
    }

    public String order(OrderRequest req) {
        return http(POST, OkxV5.API_ORDER, req);
    }

    public String closePosition(ClosePositionRequest req) {
        return http(POST, OkxV5.API_CLOSE_POSITION, req);
    }

    public String instruments(InstrumentsRequest req){
        return http(GET, OkxV5.API_INSTRUMENTS, req);
    }

    public String http(String type, String url, Object params) {
        HashMap<String, String> headMap = new HashMap<>();
        String time = Instant.now().truncatedTo(ChronoUnit.MILLIS).toString();
        headMap.put(HEADER_OK_ACCESS_KEY, okxProperties.getApikey());
        headMap.put(HEADER_OK_ACCESS_PASSPHRASE, okxProperties.getPassphrase());
        headMap.put(HEADER_OK_ACCESS_TIMESTAMP, time);
        String query;
        String body;
        if (GET.equals(type)) {
            query = prepareQuery(params);
            body = Constant.EMPTY_STRING;
        } else {
            query = Constant.EMPTY_STRING;
            body = prepareBody(params);
        }
        String signParam = String.format("%s%s%s%s%s", time, type, url, query, body);
        String sign = Base64.getEncoder().encodeToString(hmacUtils.hmac(signParam));
        log.info("Sign: {} => {}", signParam, sign);
        headMap.put(HEADER_OK_ACCESS_SIGN, sign);
        if (Boolean.TRUE.equals(okxProperties.getTestnet())) {
            headMap.put(HEADER_X_SIMULATED_TRADING, ONE_STR);
        }
        String fullUrl = BASE_URL + url + query;
        if (GET.equals(type)) {
            return httpHelper.get(fullUrl, null, headMap);
        } else {
            return httpHelper.postJson(fullUrl, body, headMap);
        }
    }

    private String prepareBody(Object params) {
        if (params == null) {
            return Constant.EMPTY_STRING;
        }
        return JSONObject.toJSONString(params);
    }

    private static String prepareQuery(Object params) {
        if (params == null) {
            return Constant.EMPTY_STRING;
        }
        StringJoiner joiner = new StringJoiner("&");
        Map<String, Object> treeMap = JSONObject.from(params).toJavaObject(new TypeReference<TreeMap<String, Object>>() {}.getType());
        for (Map.Entry<String, Object> entry : treeMap.entrySet()) {
            if (entry.getValue() != null) {
                if (entry.getValue() instanceof BigDecimal) {
                    joiner.add(entry.getKey() + "=" + ((BigDecimal) entry.getValue()).stripTrailingZeros().toPlainString());
                } else {
                    joiner.add(entry.getKey() + "=" + entry.getValue());
                }
            }
        }
        if(joiner.length() <= 0){
            return Constant.EMPTY_STRING;
        }
        return QUERY_START + joiner.toString();
    }
}
