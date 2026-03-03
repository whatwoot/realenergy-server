package com.cs.energy.member.server.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cs.web.annotation.LoginRequired;
import com.cs.web.jwt.JwtUserHolder;
import com.cs.web.util.BeanCopior;
import com.cs.energy.member.api.entity.Login;
import com.cs.energy.member.api.enums.LoginTypeEnum;
import com.cs.energy.member.api.service.LoginService;
import com.cs.energy.member.api.vo.LoginListVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.cs.sp.common.WebAssert.isNotNull;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author gpthk
 * @since 2025-02-20
 */
@Tag(name = "用户")
@RestController
@Slf4j
@RequestMapping("/api/login")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Operation(summary = "用户的账户列表（1=邮箱,2=手机,3=bsc钱包,9=PIN,等）")
    @LoginRequired
    @GetMapping("/list")
    public List<LoginListVO> list(@RequestParam(required = false) Byte type) {
        LambdaQueryWrapper<Login> query = new QueryWrapper<Login>().lambda()
                .eq(Login::getUid, JwtUserHolder.get().getId());
        if (type != null) {
            LoginTypeEnum typeEnum = LoginTypeEnum.of(type);
            isNotNull(typeEnum, "chk.common.invalid", "type");
            query.eq(Login::getType, type);
        }
        List<Login> list = loginService.list(query);
        return BeanCopior.mapList(list, LoginListVO.class);
    }
}
