
package com.cs.energy.system.server.controller;

import cn.hutool.extra.spring.SpringUtil;
import com.cs.energy.thd.api.service.HhffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author gpthk
 * @since 2024-02-08
 */
@Tag(name = "【生产环境不对外】运营接口")
@RestController
@Slf4j
@RequestMapping("/sapi/sys")
public class SysPayFlowController {

    @Autowired
    private Environment env;

    @Operation(summary = "CNY支付成功")
    @GetMapping("/payFlow/setToOk")
    public void setToOk(@RequestParam Long id) {
        SpringUtil.getBean(HhffService.class).updateToOk(id);
    }

    @Operation(summary = "CNY支付退款")
    @GetMapping("/payFlow/setToRefund")
    public void setToRefund(@RequestParam Long id) {
        SpringUtil.getBean(HhffService.class).updateToRefund(id);
    }

}


