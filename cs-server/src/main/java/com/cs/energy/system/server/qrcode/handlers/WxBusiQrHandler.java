package com.cs.energy.system.server.qrcode.handlers;

import com.cs.energy.global.constants.Gkey;
import com.cs.energy.system.server.qrcode.AbstractRuleHandler;
import com.cs.energy.system.server.qrcode.QrResult;

import java.util.Arrays;
import java.util.List;

/**
 * 微信商户二维码处理
 * eg:
 *     https://payapp.wechatpay.cn
 *     https://payapp.weixin.qq.com
 * @authro fun
 * @date 2025/4/18 05:25
 */
public class WxBusiQrHandler extends AbstractRuleHandler {
    private List<String> domains;

    public WxBusiQrHandler() {
        super(WX);
        this.domains = Arrays.asList(Gkey.WECHAT_BUSI_QR, Gkey.WECHAT_BUSI2_QR);
    }

    public WxBusiQrHandler(List<String> domains) {
        super(WX);
        this.domains = domains;
    }

    @Override
    public QrResult scan(String code) {
        for (String domain : domains) {
            if (code.startsWith(domain)) {
                // 微信整个参数做为code
                return build(code, code);
            }
        }
        return handleNext(code);
    }
}
