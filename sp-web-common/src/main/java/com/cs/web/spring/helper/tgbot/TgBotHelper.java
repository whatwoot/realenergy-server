package com.cs.web.spring.helper.tgbot;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.cs.web.spring.helper.http.HttpHelper;
import com.cs.web.spring.helper.tgbot.dto.TgBotNotifyDTO;
import com.cs.web.spring.helper.tgbot.dto.TgNotifyDTO;
import lombok.extern.slf4j.Slf4j;

/**
 * @authro fun
 * @date 2025/4/7 19:28
 */
@Slf4j
public class TgBotHelper {
    private TgBotProperties tgBotProperties;
    private HttpHelper httpHelper;

    public TgBotHelper(TgBotProperties tgBotProperties) {
        this.tgBotProperties = tgBotProperties;
        this.httpHelper = new HttpHelper();
    }

    public TgBotHelper(TgBotProperties tgBotProperties, HttpHelper httpHelper) {
        this.tgBotProperties = tgBotProperties;
        this.httpHelper = httpHelper;
    }

    /**
     * 该方法为最初始版本，独立场景使用，因为生产在用原因，暂时未下掉，不推荐使用
     * 建议使用 notifyTo(TgNotifyDTO req)
     * @see TgBotHelper#notifyTo(TgNotifyDTO)
     * @param req
     */
    @Deprecated
    public void notifyTo(TgBotNotifyDTO req) {
        if (!Boolean.TRUE.equals(tgBotProperties.getEnable())) {
            log.info("Tg-notify1 {}", JSONObject.toJSONString(req));
            return;
        }
        try {
            String body = httpHelper.postJson(tgBotProperties.getApi(), req);
            log.info("Tg-notify1 {}, {}", req.getTx(), body);
        } catch (Throwable e) {
            log.info("Tg-notify1 retry {}", req.getTx());
            try {
                // 重试一次
                String body = httpHelper.postJson(tgBotProperties.getApi(), req);
                log.info("Tg-notify1 try {}, {}", req.getTx(), body);
            } catch (Throwable ex) {
                log.warn(StrUtil.format("Tg-notify1 fail: {}", req.getTx()), e);
            }
        }
    }

    /**
     *     主要覆盖：
     *     test： 环境;
     *     scene： 场景;
     *     oriented： 消息类型面向群组。例如：开发消息：dev，运营消息: op，报表消息: hourReport 等
     *                tg通知会把相应类型的消息推给相应一个或多个群组;
     *     member： 人;
     *     things： 事，如果一行不够展示，输入的值用\n换行也是可以展示多行的;
     *     risk： 风险点;
     *     tx： 流水;
     *     createAt： 时间;
     * @param req
     */
    public void notifyTo(TgNotifyDTO req) {
        if (!Boolean.TRUE.equals(tgBotProperties.getEnable())) {
            log.info("Tg-notify {}", JSONObject.toJSONString(req));
            return;
        }
        try {
            String body = httpHelper.postJson(tgBotProperties.getApi(), req);
            log.info("Tg-notify {}, {}", req.getTx(), body);
        } catch (Throwable e) {
            log.info("Tg-notify retry {}", req.getTx());
            try {
                // 重试一次
                String body = httpHelper.postJson(tgBotProperties.getApi(), req);
                log.info("Tg-notify try {}, {}", req.getTx(), body);
            } catch (Throwable ex) {
                log.warn(StrUtil.format("Tg-notify fail: {}", req.getTx()), e);
            }
        }
    }
}
