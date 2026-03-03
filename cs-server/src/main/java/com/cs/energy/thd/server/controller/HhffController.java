package com.cs.energy.thd.server.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.cs.energy.thd.api.request.HhffCallRequest;
import com.cs.energy.thd.api.service.HhffService;
import com.cs.energy.thd.server.config.HhffHelper;
import com.cs.sp.common.CommonException;
import com.cs.web.base.BaseController;
import com.cs.web.spring.web.IgnoreResBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @authro fun
 * @date 2025/3/21 01:14
 */
@Tag(name = "第三方")
@Slf4j
@RestController
@RequestMapping("/api/thd")
public class HhffController extends BaseController {

    @Autowired
    private HhffService hhffService;

    @Operation(summary = "Hhff回调")
    @PostMapping("/hhff/notify")
    @IgnoreResBody
    public String notify(@Valid @RequestBody HhffCallRequest req) {
        try {
            hhffService.onNotify(req);
            return HhffHelper.OK;
        } catch (CommonException e) {
            String msg = getMsg(e.getCode(), e.getArgs());
            log.error(StrUtil.format("Hhff-notify fail: {}, {}", JSONObject.toJSONString(req), msg), e);
            return HhffHelper.FAIL;
        } catch (Throwable e) {
            log.error(StrUtil.format("Hhff-notify fail: {}", JSONObject.toJSONString(req)), e);
            return HhffHelper.FAIL;
        }
    }
}
