package com.cs.energy.system.server.qrcode;

/**
 * @authro fun
 * @date 2025/4/18 05:18
 */
public abstract class AbstractRuleHandler implements QrRuleHandler {
    protected String type;
    protected QrRuleHandler next;

    public AbstractRuleHandler(String type) {
        this.type = type;
    }

    @Override
    public void next(QrRuleHandler handler) {
        this.next = handler;
    }

    protected QrResult handleNext(String code) {
        if (next != null) {
            return next.scan(code);
        }
        return null;
    }

    protected QrResult build(String code, String result) {
        return new QrResult(type, this, code, result);
    }
}
