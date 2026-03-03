package com.cs.energy.system.server.qrcode.handlers;

import com.cs.energy.system.server.qrcode.AbstractRuleHandler;
import com.cs.energy.system.server.qrcode.QrResult;

/**
 * 聚合二维码处理
 *
 * @authro fun
 * @date 2025/4/18 05:25
 */
public class UnionQrHandler extends AbstractRuleHandler {
    public static final String PREFIX = "https://";

    public UnionQrHandler() {
        super(UNION);
    }

    @Override
    public QrResult scan(String code) {
        if (code.startsWith(PREFIX)) {
            // 直接整个结果做为结果
            return build(code, code);
        }
        return handleNext(code);
    }
}
