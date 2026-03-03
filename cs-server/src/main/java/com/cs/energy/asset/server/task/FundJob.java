package com.cs.energy.asset.server.task;

import cn.hutool.core.util.StrUtil;
import com.cs.energy.asset.api.service.AssetService;
import com.cs.energy.system.server.config.prop.AppProperties;
import com.cs.sp.enums.YesNoByteEnum;
import com.cs.sp.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @authro fun
 * @date 2025/10/7 15:56
 */
@Slf4j
@Component
public class FundJob {

    @Autowired
    private AssetService assetService;

    @Autowired
    private AppProperties appProperties;

    /**
     * 16点快照和发上次奖励
     */
//    @Scheduled(cron = "0 0 16 * * ?")
    public void snapForFund() {
        if(!YesNoByteEnum.YES.eq(appProperties.getTaskFlag())){
            return;
        }
        Integer ymd = DateUtil.getYmd();
        try {
            assetService.updateSnapFund(ymd);
        } catch (Throwable ex) {
            log.warn(StrUtil.format("Snap-fund {} failed", ymd), ex);
        }
    }
}
