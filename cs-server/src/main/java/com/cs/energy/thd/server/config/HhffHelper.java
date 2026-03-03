package com.cs.energy.thd.server.config;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSONObject;
import com.cs.energy.thd.api.request.hhff.*;
import com.cs.energy.thd.server.config.prop.HhffProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.StringJoiner;
import java.util.TreeMap;

/**
 * @authro fun
 * @date 2025/3/21 00:57
 */
@Slf4j
public class HhffHelper {
    public static final String SECRET_KEY = "secret";
    private HhffProperties prop;

    public static final String NAME = "hhffshop";
    public static final String OK = "success";
    public static final String FAIL = "fail";

    public static final String STATUS_OK = "dist_success";
    public static final String STATUS_NEW = "dist_new";
    public static final String STATUS_ING = "dist_operating";
    public static final String STATUS_FAIL = "dist_fail";

    public static final String PAY_API = "/api/v2/distributes";
    public static final String PAY_QUERY_API = "/api/v2/distributes/";
    public static final String REPORT_QUERY_API = "/api/v2/distributes/report";

    public static final String NO_ORDER_ERROR = "1010404";

    public static final DecimalFormat df = new DecimalFormat("#.00");

    public HhffHelper(HhffProperties prop) {
        this.prop = prop;
    }


    public OrderRes query(PayQuery query) {
        query.setMerchantId(prop.getMerchantId());
        query.setTimestamp(System.currentTimeMillis());
        TreeMap<String, Object> params = toMap(query);
        String sign = sign(params);
        params.put("sign", sign);
        String url = prop.getApi() + PAY_QUERY_API + query.getDistributeCode();
        String body = null;
        try {
            body = HttpRequest.get(url)
                    .form(params)
                    .setReadTimeout(10000)
                    .setConnectionTimeout(5000)
                    .execute().body();
            log.info("Hhff-query param: {} res: {}", JSONObject.toJSONString(params), body);
            return JSONObject.parseObject(body, OrderRes.class);
        } catch (Throwable e) {
            log.warn(StrUtil.format("Hhff-order {} fail: {}", query.getDistributeCode(), StringUtils.truncate(body, 200)), e);
        }
        return null;
    }

    public ReportRes reportQuery(ReportQuery query) {
        query.setMerchantId(prop.getMerchantId());
        query.setTimestamp(System.currentTimeMillis());
        TreeMap<String, Object> params = toMap(query);
        String sign = sign(params);
        params.put("sign", sign);
        String url = prop.getApi() + REPORT_QUERY_API;
        String body = null;
        try {
            body = HttpRequest.get(url)
                    .form(params)
                    .execute().body();
            return JSONObject.parseObject(body, ReportRes.class);
        } catch (Throwable e) {
            log.warn(StrUtil.format("Hhff-report {} fail: {}", query.getDate(), StringUtils.truncate(body, 200)), e);
        }
        return null;
    }

    public String pay(String serverUrl, Pay pay) {
        pay.setMerchantId(prop.getMerchantId());
        pay.setTimestamp(System.currentTimeMillis());
        pay.setNotificationUrl(serverUrl + "/api/thd/hhff/notify");
        TreeMap<String, Object> params = toMap(pay);
        String sign = sign(params);
        params.put("sign", sign);
        String url = prop.getApi() + PAY_API;
        String body = JSONObject.toJSONString(params);
        log.info("Hhff-pay {} url:{}, param: {}", pay.getDistributeCode(), url, body);
        HttpResponse http = null;
        String resp = null;
        try {
            http = HttpRequest.post(url)
                    .body(body, ContentType.JSON.getValue())
                    .execute();
            resp = http.body();
            return resp;
        } finally {
            if (http != null) {
                log.info("Hhff-pay {} {} res:{}", pay.getDistributeCode(), http.getStatus(), StringUtils.truncate(resp, 300));
            }
        }
    }

    public boolean verifySign(BasePay param) {
        TreeMap<String, Object> map = toMap(param);
        Object signObj = map.remove("sign");
        if (signObj == null) {
            return false;
        }
        String sign = sign(map);
        return sign.equals(signObj.toString());
    }

    public String sign(BasePay signPay) {
        return sign(toMap(signPay), prop.getSecret());
    }

    public String sign(TreeMap<String, Object> treeMap) {
        return sign(treeMap, prop.getSecret());
    }

    public static TreeMap<String, Object> toMap(BasePay req) {
        JSONObject jsonObject = JSONObject.from(req);
        return new TreeMap<>(jsonObject);
    }

    public static String sign(TreeMap<String, Object> treeMap, String secret) {
        return sign(treeMap, secret, SECRET_KEY);
    }

    /**
     * 签名方法对bigdecimal在1.0和1.00得到的字符串不一致，所以单独处理一下
     *
     * @param treeMap
     * @param secret
     * @param secretKey
     * @return
     */
    public static String sign(TreeMap<String, Object> treeMap, String secret, String secretKey) {
        StringJoiner joiner = new StringJoiner("&");
        for (Map.Entry<String, Object> entry : treeMap.entrySet()) {
            if (entry.getValue() != null) {
                if (entry.getValue() instanceof BigDecimal) {
                    BigDecimal bigDecimal = (BigDecimal) entry.getValue();
                    joiner.add(String.format("%s=%s", entry.getKey(), df.format(bigDecimal)));
                } else {
                    joiner.add(String.format("%s=%s", entry.getKey(), entry.getValue()));
                }
            }
        }
        joiner.add(String.format("%s=%s", secretKey, secret));
        String paramStr = joiner.toString();
        String sign = DigestUtils.md5Hex(paramStr).toUpperCase();
        log.info("Hhff-sign: {}, sign {}", paramStr, sign);
        return sign;
    }
}
