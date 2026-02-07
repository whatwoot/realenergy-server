package com.cs.copy.system.server.helper;

import cn.hutool.http.ContentType;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson2.JSONObject;
import com.cs.copy.system.server.helper.prop.TurnstileProperties;
import com.cs.web.base.BaseCfRequest;
import com.cs.web.util.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

import static com.cs.sp.common.WebAssert.expect;

/**
 * @author fiona
 * @date 2025/2/21 19:44
 */
@Slf4j
public class TurnstileHelper {

    private TurnstileProperties turnstileProperties;

    public TurnstileHelper(TurnstileProperties turnstileProperties) {
        this.turnstileProperties = turnstileProperties;
    }

    public void verify(BaseCfRequest req) {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attr.getRequest();
        JSONObject json = new JSONObject();
        String ip = IpUtils.getIpAddr(request);
        json.put("ip", ip);
        json.put("response", req.getTurnstile());
        json.put("secret", turnstileProperties.getSecret());
        String body = HttpRequest.post(turnstileProperties.getVerifyUrl())
                .body(json.toJSONString(), ContentType.JSON.getValue())
                .execute().body();
        JSONObject result = JSONObject.parseObject(body);
        boolean ok = Boolean.TRUE.equals(result.getBoolean("success"));
        if(!ok){
            log.info("Turnstile {} {}", ip, body);
        }
        expect(ok, "chk.common.captchaFail");
    }
}
