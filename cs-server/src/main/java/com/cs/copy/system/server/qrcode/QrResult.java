package com.cs.copy.system.server.qrcode;

import lombok.*;

/**
 * @authro fun
 * @date 2025/4/18 05:15
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QrResult {
    /**
     * 类型
     * @see QrRuleHandler
     */
    private String type;

    private QrRuleHandler handler;
    /**
     * 原始码
     */
    private String code;
    /**
     * 识别结果
     */
    private String result;
}
