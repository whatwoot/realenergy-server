package com.cs.energy.system.server.qrcode.handlers;

import com.cs.energy.global.constants.Gkey;
import com.cs.energy.system.server.qrcode.AbstractRuleHandler;
import com.cs.energy.system.server.qrcode.QrResult;

import java.util.Arrays;
import java.util.List;

import static com.cs.sp.common.WebAssert.expect;

/**
 * 微信个人二维码处理
 * eg:
 *     wxp://
 *     wxp://f2f6 这种是截图的黄色的码，只有面对面可以付，系统付不了
 * @authro fun
 * @date 2025/4/18 05:25
 */
public class WxPersonalQrHandler extends AbstractRuleHandler {
    private List<String> domains;
    // 微信有黑名单
    private String blacks;

    public WxPersonalQrHandler() {
        super(WX);
        this.domains = Arrays.asList(Gkey.WECHAT_QR);
        this.blacks = Gkey.WECHAT_QR_SCREEN;
    }

    public WxPersonalQrHandler(List<String> domains, String blacks) {
        super(WX);
        this.domains = domains;
        this.blacks = blacks;
    }

    @Override
    public QrResult scan(String code) {
        QrResult wx = null;
        for (String domain : domains) {
            if (code.startsWith(domain)) {
                // 微信整个参数做为code
                wx = build(code, code);
                break;
            }
        }
        if (wx != null) {
            if (blacks != null) {
                expect(!code.startsWith(blacks), "chk.merchant.noScreenshot");
            }
            return wx;
        }
        return handleNext(code);
    }
}
