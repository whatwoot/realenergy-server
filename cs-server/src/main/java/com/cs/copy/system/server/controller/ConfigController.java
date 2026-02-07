package com.cs.copy.system.server.controller;

import com.cs.copy.system.api.entity.Config;
import com.cs.copy.system.api.service.ConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author gpthk
 * @since 2024-02-08
 */
@Tag(name = "【测试用】系统调试相关接口")
@RestController
@RequestMapping("/sapi/config")
public class ConfigController {

    @Autowired
    private ConfigService configService;


    @Operation(summary = "配置列表")
    @GetMapping("/list")
    public List<Config> list(String category) {
        return configService.getByCategory(category);
    }

    @Operation(summary = "配置信息")
    @GetMapping("/cateKey")
    public Config key(String category, String key) {
        return configService.getByCategoryAndKey(category, key);
    }

    @Operation(summary = "配置信息")
    @GetMapping("/key")
    public Config key(String key) {
        return configService.getByKey(key);
    }

}
