package com.cs.energy.system.server.qrcode.handlers;

import com.cs.energy.system.server.qrcode.AbstractRuleHandler;
import com.cs.energy.system.server.qrcode.QrResult;

import java.util.Set;

/**
 * 自有商户二维码处理
 *
 * @authro fun
 * @date 2025/4/18 05:25
 */
public class MerchantQrHandler extends AbstractRuleHandler {
    private Set<String> domains;
    // 要过滤时间随机函数
    private Boolean filterSearch;

    public MerchantQrHandler(Set<String> domains, Boolean filterSearch) {
        super(SELF);
        this.domains = domains;
        this.filterSearch = filterSearch;
    }

    @Override
    public QrResult scan(String code) {
        String result = null;
        for (String domain : domains) {
            if (code.startsWith(domain)) {
                // code直接取域名后面的。要多一个/
                result = code.substring(domain.length() + 1);
                break;
            }
        }
        if (result != null) {
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
