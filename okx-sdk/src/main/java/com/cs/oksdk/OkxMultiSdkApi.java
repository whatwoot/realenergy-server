package com.cs.oksdk;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.cs.oksdk.config.OkxHttpHelper;
import com.cs.oksdk.constant.Constant;
import com.cs.oksdk.dto.CopyMember;
import com.cs.oksdk.reponse.*;
import com.cs.oksdk.request.*;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
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
public class OkxMultiSdkApi {
    private OkxHttpHelper httpHelper;
    private boolean simulated;
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
    public static final String JSON_START = "{";
    public static final String ERR_FORMAT =  "{\"code\":500,\"msg\":\"%s\"}";

    private final Cache<String, HmacUtils> SIGN_MAP = Caffeine.newBuilder()
            .maximumSize(10000000)
            .recordStats()
            .build();

    public OkxMultiSdkApi() {
        this.simulated = false;
        this.httpHelper = new OkxHttpHelper(OkxHttpHelper.createSuddenClient());
    }

    public OkxMultiSdkApi(OkxHttpHelper httpHelper) {
        this.simulated = false;
        this.httpHelper = httpHelper;
    }

    public OkxMultiSdkApi(boolean simulated) {
        this.simulated = simulated;
        this.httpHelper = new OkxHttpHelper(OkxHttpHelper.createSuddenClient());
    }

    public OkxMultiSdkApi(boolean simulated, OkxHttpHelper httpHelper) {
        this.simulated = simulated;
        this.httpHelper = httpHelper;
    }

    public String getStatus() {
        return httpWithTry(null, GET, OkxV5.API_STATUS, null);
    }

    public InstrumentRes instruments(InstrumentsRequest req){
        String s = httpWithTry(null, GET, OkxV5.API_INSTRUMENTS, req);
        return JSONObject.parseObject(s, InstrumentRes.class);
    }

    public OrderRes order(CopyMember prop, OrderRequest req) {
        String s = httpWithTry(prop, POST, OkxV5.API_ORDER, req);
        log.debug("Order: {}", s);
        return JSONObject.parseObject(s, OrderRes.class);
    }

    public GetOrderRes getOrder(CopyMember prop, GetOrderRequest req) {
        String s = httpWithTry(prop, GET, OkxV5.API_ORDER, req);
        log.debug("getOrder: {}", s);
        return JSONObject.parseObject(s, GetOrderRes.class);
    }

    public GetOrderRes getOrderHis(CopyMember prop, GetOrderHisRequest req) {
        String s = httpWithTry(prop, GET, OkxV5.API_ORDER_HIS, req);
        log.debug("getOrderHis: {}", s);
        return JSONObject.parseObject(s, GetOrderRes.class);
    }

    public GetOrderRes getOrderHisArc(CopyMember prop, GetOrderHisRequest req) {
        String s = httpWithTry(prop, GET, OkxV5.API_ORDER_HIS_ARC, req);
        log.debug("getOrderHisArc: {}", s);
        return JSONObject.parseObject(s, GetOrderRes.class);
    }

    /**
     * resp eg:
     *  {"code":"0","data":[{"clOrdId":"","instId":"ETH-USDT-SWAP","posSide":"long","tag":""}],"msg":""}
     * @param prop
     * @param req
     * @return
     */
    public ClosePositionRes closePosition(CopyMember prop, ClosePositionRequest req){
        String http = httpWithTry(prop, POST, OkxV5.API_CLOSE_POSITION, req);
        log.info("closePosition {}", http);
        return JSONObject.parseObject(http, ClosePositionRes.class);
    }

    public PositionsRes positions(CopyMember prop, PositionsRequest req){
        String http = httpWithTry(prop, GET, OkxV5.API_POSITIONS, req);
        log.info("positions {}", http);
        return JSONObject.parseObject(http, PositionsRes.class);
    }

    public PositionsHisRes positionsHis(CopyMember prop, PositionsHisRequest req){
        String http = httpWithTry(prop, GET, OkxV5.API_POSITIONS_HIS, req);
        log.debug("positionsHis {}", http);
        return JSONObject.parseObject(http, PositionsHisRes.class);
    }

    public PositionsHisRes fills(CopyMember prop, FillsRequest req){
        String http = httpWithTry(prop, GET, OkxV5.API_FILLS, req);
        log.debug("fills {}", http);
        return JSONObject.parseObject(http, PositionsHisRes.class);
    }

    public AssetBillsRes assetBills(CopyMember prop, AssetBillsRequest req){
        String http = httpWithTry(prop, GET, OkxV5.API_ASSET_BILLS, req);
        log.debug("bills {}", http);
        return JSONObject.parseObject(http, AssetBillsRes.class);
    }

    public AssetBillsRes assetBillsHis(CopyMember prop, AssetBillsRequest req){
        String http = httpWithTry(prop, GET, OkxV5.API_ASSET_BILLS_HIS, req);
        log.debug("bills {}", http);
        return JSONObject.parseObject(http, AssetBillsRes.class);
    }

    public AccountBillsRes accountBills(CopyMember prop, AccountBillsRequest req){
        String http = httpWithTry(prop, GET, OkxV5.API_ACCOUNT_BILLS, req);
        log.debug("accountBills {}", http);
        return JSONObject.parseObject(http, AccountBillsRes.class);
    }

    public AccountConfigRes accountConfig(CopyMember prop){
        String http = httpWithTry(prop, GET, OkxV5.API_ACCOUNT_CONFIG, null);
        log.debug("accountConfig {}", http);
        return JSONObject.parseObject(http, AccountConfigRes.class);
    }

    public BalanceRes balances(CopyMember prop, BalanceRequest req){
        String http = httpWithTry(prop, GET, OkxV5.API_BALANCE, req);
        log.debug("balances {}", http);
        return JSONObject.parseObject(http, BalanceRes.class);
    }

    public LeverageInfoRes leverageInfo(CopyMember prop, LeverageInfoRequest req){
        String s = httpWithTry(prop, GET, OkxV5.API_LEVERAGE_INFO, req);
        return JSONObject.parseObject(s, LeverageInfoRes.class);
    }

    public SetLeverageInfoRes setLeverage(CopyMember prop, SetLeverageRequest req){
        String s = httpWithTry(prop, POST, OkxV5.API_SET_LEVERAGE, req);
        return JSONObject.parseObject(s, SetLeverageInfoRes.class);
    }

    public SetPositionModeRes setPositionMode(CopyMember prop, SetPositionModeRequest req){
        String s = httpWithTry(prop, POST, OkxV5.API_SET_POSITION_MODE, req);
        return JSONObject.parseObject(s, SetPositionModeRes.class);
    }

    public SetAccountLevelRes setAccountLevel(CopyMember prop, SetAccountLevelRequest req){
        String s = httpWithTry(prop, POST, OkxV5.API_SET_ACCOUNT_LEVEL, req);
        log.info("setAccountLevel {}", s);
        return JSONObject.parseObject(s, SetAccountLevelRes.class);
    }

    public String httpWithTry(CopyMember user, String type, String url, Object params){
        try {
            return http(user, type, url, params);
        } catch (Throwable e) {
            log.warn(StrUtil.format("httpWithTry {}", user.getId()), e);
            String msg = e.getMessage();
            if(!StrUtil.isNotBlank(msg)){
                if(e.getCause() != null){
                    msg = e.getCause().getMessage();
                }else{
                    msg = e.toString();
                }
            }
            return !msg.startsWith(JSON_START) ? String.format(ERR_FORMAT, StrUtil.truncateUtf8(msg, 50)) : msg;
        }
    }

    public String http(CopyMember user, String type, String url, Object params) {
        String query;
        String body;
        if (GET.equals(type)) {
            query = prepareQuery(params);
            body = Constant.EMPTY_STRING;
        } else {
            query = Constant.EMPTY_STRING;
            body = prepareBody(params);
        }
        HashMap<String, String> headMap = new HashMap<>();
        if(user != null){
            HmacUtils hmacUtils = SIGN_MAP.getIfPresent(user.getApikey());
            if (hmacUtils == null) {
                hmacUtils = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, user.getSecret());
                SIGN_MAP.put(user.getApikey(), hmacUtils);
            }
            String time = Instant.now().truncatedTo(ChronoUnit.MILLIS).toString();
            headMap.put(HEADER_OK_ACCESS_KEY, user.getApikey());
            headMap.put(HEADER_OK_ACCESS_PASSPHRASE, user.getPassphrase());
            headMap.put(HEADER_OK_ACCESS_TIMESTAMP, time);
            String signParam = String.format("%s%s%s%s%s", time, type, url, query, body);
            String sign = Base64.getEncoder().encodeToString(hmacUtils.hmac(signParam));
            log.debug("Sign: {} => {}", signParam, sign);
            headMap.put(HEADER_OK_ACCESS_SIGN, sign);
            if (Boolean.TRUE.equals(simulated)) {
                headMap.put(HEADER_X_SIMULATED_TRADING, ONE_STR);
            }
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
