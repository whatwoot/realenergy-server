package com.cs.energy.system.server.qrcode;

/**
 * 责任链构造器
 * @authro fun
 * @date 2025/4/18 05:57
 */
public class QrScanChainBuilder {
    private QrRuleHandler head;
    private QrRuleHandler tail;

    public QrScanChainBuilder add(QrRuleHandler handler) {
        if (head == null) {
            head = handler;
            tail = handler;
        } else {
            tail.next(handler);
            tail = handler;
        }
        return this;
    }

    public QrRuleHandler build() {
        return head;
    }
}
