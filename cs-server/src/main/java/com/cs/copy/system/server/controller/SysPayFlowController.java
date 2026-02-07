
package com.cs.copy.system.server.controller;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cs.copy.global.constants.CacheKey;
import com.cs.copy.member.api.entity.Login;
import com.cs.copy.member.api.entity.Member;
import com.cs.copy.member.api.enums.LoginTypeEnum;
import com.cs.copy.member.api.service.LoginService;
import com.cs.copy.member.api.service.MemberService;
import com.cs.copy.member.api.vo.UserDetailVO;
import com.cs.copy.system.server.config.prop.AppProperties;
import com.cs.copy.thd.api.service.HhffService;
import com.cs.sp.common.BeanCopior;
import com.cs.sp.enums.YesNoByteEnum;
import com.cs.web.jwt.JwtUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.crypto.WalletUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;

import static com.cs.sp.common.WebAssert.*;

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


