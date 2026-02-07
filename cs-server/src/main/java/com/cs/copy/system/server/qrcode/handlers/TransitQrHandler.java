package com.cs.copy.system.server.qrcode.handlers;

import com.cs.copy.system.server.qrcode.AbstractRuleHandler;
import com.cs.copy.system.server.qrcode.QrResult;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 自有中转类的商户二维码处理
 *
 * @authro fun
 * @date 2025/4/18 05:25
 */
public class TransitQrHandler extends AbstractRuleHandler {
    /**
     * 中转域名/qr.html
     */
    private Set<String> prefixs;

    public static final Pattern pattern = Pattern.compile("[?&]code=([^&]*)");

    public TransitQrHandler(Set<String> domains) {
        super(SELF);
        this.prefixs = domains;
    }

    /**
     * eg:
     * https://tnomc.s3.ap-southeast-1.amazonaws.com/qr.html?code=${code}
     * https://tnomc.s3.ap-southeast-1.amazonaws.com/qr.html?code=${code}&t=xxx
     * @param code
     * @return
     */
    @Override
    public QrResult scan(String code) {
        for (String domain : prefixs) {
            if (code.startsWith(domain)) {
                Matcher matcher = pattern.matcher(code);
                if (matcher.find()) {
                    return build(code, matcher.group(1));
                }
            }
        }
        return handleNext(code);
    }
}
