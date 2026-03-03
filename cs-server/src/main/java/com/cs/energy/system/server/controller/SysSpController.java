package com.cs.energy.system.server.controller;

import com.cs.web.spring.config.prop.SpProperties;
import com.cs.web.util.BeanCopior;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
public class SysSpController {

    @Autowired
    private Environment env;

    @Autowired
    private SpProperties spProperties;

    @Operation(summary = "查看App配置 ")
    @GetMapping("/getSpProp")
    public SpProperties getSpProp() {
        return spProperties;
    }

    @Operation(summary = "设置App檲 ")
    @GetMapping("/setSpProp")
    public SpProperties setAppProp(SpProperties prop) {
        BeanCopior.copy(prop, spProperties);
        return spProperties;
    }
}

