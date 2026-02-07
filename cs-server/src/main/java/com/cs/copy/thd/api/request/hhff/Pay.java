package com.cs.copy.thd.api.request.hhff;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

/**
 * @authro fun
 * @date 2025/4/1 14:56
 */
@Data
public class Pay extends BasePay {
    @JSONField(name = "distribute_code")
    private Long distributeCode;
    private String paytool;
    private String amount;
    private String cardholder;
    private String cardnumber;
    private String bank;
    private String paymentcode;
    @JSONField(name = "notification_url")
    private String notificationUrl;
}
