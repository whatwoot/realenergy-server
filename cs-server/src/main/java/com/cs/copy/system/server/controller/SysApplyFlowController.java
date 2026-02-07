
package com.cs.copy.system.server.controller;

import com.cs.copy.system.api.entity.ApplyFlow;
import com.cs.copy.system.api.service.ApplyFlowService;
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
public class SysApplyFlowController {

    @Autowired
    private Environment env;

    @Autowired
    private ApplyFlowService applyFlowService;

    @Operation(summary = "审核")
    @GetMapping("/applyFlow/audit")
    public void applyFlowAudit(@RequestParam Long id,
                               @RequestParam Byte status,
                               @RequestParam(required = false) String msg
    ) {
        ApplyFlow req = new ApplyFlow();
        req.setId(id);
        req.setStatus(status);
        req.setAuditMsg(msg);
        applyFlowService.updateAudit(req);
    }
}

