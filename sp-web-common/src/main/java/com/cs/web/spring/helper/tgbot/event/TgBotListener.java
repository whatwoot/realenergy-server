package com.cs.web.spring.helper.tgbot.event;

import cn.hutool.extra.spring.SpringUtil;
import com.cs.sp.constant.Constant;
import com.cs.web.spring.helper.tgbot.TgBotHelper;
import com.cs.web.spring.helper.tgbot.dto.TgNotifyDTO;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.scheduling.annotation.Async;

/**
 * @authro fun
 * @date 2025/4/25 03:12
 */
public class TgBotListener {

    private TgBotHelper tgBotHelper;

    public TgBotListener(TgBotHelper tgBotHelper) {
        this.tgBotHelper = tgBotHelper;
    }

    @EventListener
    @Async
    public void notify(TgNotifyEvent e) {
        TgNotifyDTO notify = e.getNotify();
        notify.setTest(!SpringUtil.getBean(Environment.class).acceptsProfiles(Profiles.of(Constant.PROD)));
        tgBotHelper.notifyTo(notify);
    }
}
