package com.cs.energy.system.server.controller;

import com.cs.energy.system.api.service.SseService;
import com.cs.web.jwt.JwtUserHolder;
import com.cs.web.spring.web.IgnoreResBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author gpthk
 * @since 2024-03-01
 */
@Tag(name = "SSE推送")
@RestController
@RequestMapping("/sse")
public class SseController {

    @Autowired
    private SseService sseService;

    @Operation(summary = "连接")
    @IgnoreResBody
    @GetMapping(value = "/", produces = "text/event-stream;charset=UTF-8")
    public SseEmitter connect(HttpServletRequest request) {
        Long id = null;
        if(JwtUserHolder.get() != null) {
            id = JwtUserHolder.get().getId();
        }
        return sseService.addNew(request.getSession().getId(), id);
    }
}
