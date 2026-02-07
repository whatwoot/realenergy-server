package com.cs.copy.system.server.controller;

import com.cs.copy.system.api.entity.JsReport;
import com.cs.copy.system.api.service.JsReportService;
import com.cs.copy.system.api.dto.JsReportDTO;
import com.cs.web.spring.web.IgnoreResBody;
import com.cs.web.util.BeanCopior;
import com.cs.web.util.IpUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author gpthk
 * @since 2024-03-27
 */
@Tag(name = "上报")
@RestController
@RequestMapping("/api/js")
public class JsReportController {

    @Autowired
    private JsReportService jsReportService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Operation(summary = "错误上报")
    @PostMapping("/u")
    @IgnoreResBody
    public void report(@RequestBody JsReportDTO param, HttpServletRequest req) {
        String ip = IpUtils.getIpAddr(req);
        JsReport js = BeanCopior.map(param, JsReport.class);
        js.setIp(ip);
        jsReportService.save(js);
    }
}
