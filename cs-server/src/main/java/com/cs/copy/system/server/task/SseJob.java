package com.cs.copy.system.server.task;

import com.cs.copy.system.api.service.SseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 用于保持sse连接活跃的全量心跳
 * @author fiona
 * @date 2024/12/17 22:06
 */
@Slf4j
@Component
public class SseJob {

    @Autowired
    private SseService sseService;

    public void heartbeat() {
        try {
            long l = sseService.sendAll("pong", System.currentTimeMillis());
            log.info("heartbeat {}", l);
        } catch (Exception e) {
            log.info("heartbeat fail", e);
        }
    }
}
