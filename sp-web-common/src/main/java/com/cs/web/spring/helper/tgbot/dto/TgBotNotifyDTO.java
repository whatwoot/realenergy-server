package com.cs.web.spring.helper.tgbot.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @authro fun
 * @date 2025/4/7 19:29
 */
@Deprecated
@Data
public class TgBotNotifyDTO extends BaseTgDTO {
    private Boolean test;
    private Boolean merchant;
    private String user;
    private String chain;
    private BigDecimal amount;
    private String symbol;
    private Long createAt;
}
