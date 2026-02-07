package com.cs.copy.member.server.controller;

import com.cs.copy.member.api.service.MemberWalletService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author gpthk
 * @since 2025-02-24
 */
@Tag(name = "用户")
@RestController
@RequestMapping("/api/memberWallet")
public class MemberWalletController {

    @Autowired
    private MemberWalletService memberWalletService;


}
