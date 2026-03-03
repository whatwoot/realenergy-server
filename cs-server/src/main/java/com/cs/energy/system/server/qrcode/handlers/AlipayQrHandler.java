package com.cs.energy.system.server.qrcode.handlers;

import com.cs.energy.global.constants.Gkey;
import com.cs.energy.system.server.qrcode.AbstractRuleHandler;
import com.cs.energy.system.server.qrcode.QrResult;

import java.util.Arrays;
import java.util.List;

/**
 * 支付宝二维码处理
 * eg:
 *    https://qr.alipay.com/xxxxx
 *    https://qr.alipay.com/xxxxxx?t=123213123
 * @authro fun
 * @date 2025/4/18 05:25
 */
public class AlipayQrHandler extends AbstractRuleHandler {
    private List<String> domains;
    // 支付宝要过滤时间随机时间参数
    private Boolean filterSearch;

    /**
     * 默认的一些
     */
    public AlipayQrHandler() {
        super(ALIPAY);
        this.domains = Arrays.asList(Gkey.ALIPAY_QR, Gkey.ALIPAY_QR_UPPER);
        this.filterSearch = true;
    }

    /**
     * 支持自定义一些
     *
     * @param domains
     * @param filterSearch
     */
    public AlipayQrHandler(List<String> domains, Boolean filterSearch) {
        super(ALIPAY);
        this.domains = domains;
        this.filterSearch = filterSearch;
    }

    @Override
    public QrResult scan(String code) {
        String result = null;
        for (String domain : domains) {
            if (code.startsWith(domain)) {
                result = code;
                break;
            }
        }
        if (result != null) {
            // alipay的二级码有随机参数，不参与结果，否则会被限制支付时间
            if (Boolean.TRUE.equals(filterSearch)) {
                int idx = result.indexOf("?");
                if (idx > -1) {
                    result = result.substring(0, idx);
                }
            }
            return build(code, result);
        }
        return handleNext(code);
    }
}
