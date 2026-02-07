package com.cs.copy.system.server.helper;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.cs.copy.system.server.helper.prop.GeetestProperties;
import com.cs.copy.system.server.helper.prop.GeetestRequest;
import com.cs.sp.common.BeanCopior;
import com.cs.web.base.BaseGeetestRequest;
import com.cs.web.base.BaseUnionTestRequest;
import com.cs.web.spring.helper.http.HttpHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.cs.sp.common.WebAssert.*;

/**
 * @author fiona
 * @date 2025/2/21 19:44
 */
@Slf4j
public class GeetestHelper {

    public static final String OK = "success";
    public static final String KEY = "result";

    private Map<String, GeetestProperties> idMap;
    private HttpHelper httpHelper;

    public GeetestHelper(Map<String, GeetestProperties> propMap) {
        this.idMap = idMap(propMap);
        this.httpHelper = new HttpHelper();
    }

    public GeetestHelper(Map<String, GeetestProperties> propMap, HttpHelper httpHelper) {
        this.idMap = idMap(propMap);
        this.httpHelper = httpHelper;
    }

    public GeetestHelper(GeetestProperties geetestProperties, HttpHelper httpHelper) {
        idMap = new HashMap<>();
        idMap.put(geetestProperties.getCaptchaId(), geetestProperties);
        this.httpHelper = httpHelper;
    }

    public GeetestHelper(GeetestProperties geetestProperties) {
        idMap = new HashMap<>();
        idMap.put(geetestProperties.getCaptchaId(), geetestProperties);
        this.httpHelper = new HttpHelper();
    }

    public Map<String, GeetestProperties> idMap(Map<String, GeetestProperties> beanMap) {
        return beanMap.values().parallelStream().collect(Collectors.toMap(
                GeetestProperties::getCaptchaId,
                g -> g,
                (existing, replacement) -> replacement
        ));
    }

    public void verify(BaseUnionTestRequest req) {
        GeetestRequest gee = BeanCopior.map(req, GeetestRequest.class);
        union(gee);
    }

    public void verify(BaseGeetestRequest req) {
        GeetestRequest gee = BeanCopior.map(req, GeetestRequest.class);
        union(gee);
    }

    private void union(GeetestRequest req) {
        isNotBlank(req.getCaptchaId(), "chk.common.required", "captchaId");
        GeetestProperties prop = idMap.get(req.getCaptchaId());
        expectNotNull(prop, "chk.geetest.notSupport");
        String signToken = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, prop.getCaptchaKey()).hmacHex(req.getLotNumber());
        Boolean ok = null;
        String result = null;
        try {
            req.setSignToken(signToken);
            result = httpHelper.postForm(String.format("%s/validate", prop.getApi()), req);
            JSONObject json = JSONObject.parseObject(result);
            ok = OK.equals(json.getString(KEY));
            if(!ok){
                log.warn("Geetest-validate {}", result);
            }
        } catch (Throwable e) {
            log.warn(StrUtil.format("Geetest-validate error: {}", result), e);
        }
        // ok为null表示服务响应失败或响应非标准json，要静默让过
        if (Boolean.FALSE.equals(ok)) {
            throwParamException("chk.geetest.fail");
        }
    }
}
