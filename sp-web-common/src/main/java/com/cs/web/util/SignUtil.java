package com.cs.web.util;

import com.cs.sp.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 签名工具类
 */
@Slf4j
public class SignUtil {

    /**
     * 验证签名
     * 验证算法：把timestamp + JsonUtil.object2Json(SortedMap)合成字符串，然后MD5
     */
    public static boolean verifySign(String url, SortedMap<String, String> map, String sign, Long timestamp) {
        if(map == null){
            map = new TreeMap<>();
        }
        String params = timestamp + url + JsonUtil.object2Json(map);
        return verifySign(params, sign);
    }

    /**
     * 验证签名
     */
    public static boolean verifySign(String params, String sign) {
        if (StringUtils.isEmpty(params)) {
            return false;
        }
        String paramsSign = DigestUtils.md5DigestAsHex(params.getBytes()).toLowerCase();
        boolean eq = sign.equals(paramsSign);
        if(!eq){
            log.info("Sign-eq: {}, {}=>{}", params, paramsSign, sign);
        }
        return eq;
    }

}
