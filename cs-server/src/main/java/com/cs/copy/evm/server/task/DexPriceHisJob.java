package com.cs.copy.evm.server.task;

import com.cs.copy.evm.api.service.DecPriceHisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @authro fun
 * @date 2026/1/4 18:04
 */
@Slf4j
@Component
public class DexPriceHisJob {

    @Autowired
    private DecPriceHisService decPriceHisService;

    //@Scheduled(fixedDelay = 3000, initialDelay = 3000)
    public void dexPriceHisJob() {
        try {
            decPriceHisService.scanHis();
        } catch (Throwable e) {
            log.warn("Dex-price-fail", e);
        }
    }
}
