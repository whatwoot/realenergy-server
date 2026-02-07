package com.cs.web.spring.helper.tgbot.dto;

import com.cs.sp.common.base.BaseDTO;
import lombok.Data;

/**
 * @authro fun
 * @date 2025/4/7 19:29
 */
@Data
public class TgNotifyDTO extends BaseDTO {
    private Boolean test;
    // 场景
    private String scene;
    // 通知人群：dev or op
    // 多个群：
    private String oriented;
    // 人
    private String member;
    // 事
    private String things;
    // 风险
    private String risk;
    // 流水id
    private String tx;
    // 时间
    private Long createAt;
}
