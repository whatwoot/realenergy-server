package com.cs.copy.system.server.qrcode;

/**
 * @authro fun
 * @date 2025/4/18 05:16
 */
public interface QrRuleHandler {
    String WX = "wx";
    String ALIPAY = "alipay";
    String SELF = "self";
    String UNION = "union";

    QrResult scan(String code);
    void next(QrRuleHandler handler);
}
