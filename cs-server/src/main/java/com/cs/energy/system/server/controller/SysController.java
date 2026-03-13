package com.cs.energy.system.server.controller;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import com.cs.energy.global.constants.CacheKey;

import com.cs.energy.system.api.entity.Config;
import com.cs.energy.system.api.enums.CacheKeyEnum;
import com.cs.energy.system.api.enums.SseNameEnum;
import com.cs.energy.system.api.event.PropRefreshEvent;
import com.cs.energy.system.api.event.ReBuildCacheEvent;
import com.cs.energy.system.api.event.ReFreshEnergyPoolEvent;
import com.cs.energy.system.api.event.RefreshConfigEvent;
import com.cs.energy.system.api.service.ConfigService;
import com.cs.energy.system.api.service.SseService;
import com.cs.energy.system.server.config.prop.AppProperties;
import com.cs.sp.enums.YesNoByteEnum;
import com.cs.sp.enums.YesNoIntEnum;
import com.cs.sp.util.StringUtil;
import com.cs.web.spring.helper.aeshelper.AesHelper;
import com.cs.web.spring.helper.hashids.HashidsHelper;
import com.cs.web.spring.helper.rsahelper.RsaHelper;
import com.cs.web.spring.helper.tgbot.dto.TgNotifyDTO;
import com.cs.web.spring.helper.tgbot.event.TgNotifyEvent;
import com.cs.web.util.BeanCopior;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Keys;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.core.Response;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.utils.Numeric;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
public class SysController {

    @Autowired
    private Environment env;

    @Autowired
    private ConfigService configService;


    @Autowired
    private SseService sseService;


    @Autowired
    private AesHelper aesHelper;


    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private HashidsHelper hashidsHelper;


    @Autowired
    private RsaHelper rsaHelper;



    @Autowired
    private AppProperties appProperties;

    @Operation(summary = "配置参数")
    @GetMapping("/config")
    public GlobalConfig config() {
        GlobalConfig globalConfig = configService.getGlobalConfig(GlobalConfig.class);
        return globalConfig;
    }

    @Operation(summary = "RSA解密")
    @GetMapping("/rsa/decrypt")
    public String decrypt(@RequestParam String input) {
        return rsaHelper.decrypt(input);
    }

    @Operation(summary = "即将维护(0=加载中，1=正常，2=维护")
    @GetMapping("/setMaintain")
    public void setMaintain(@RequestParam String status) {
        stringRedisTemplate.opsForValue().set(CacheKey.GAME_STAUS, status);
    }

    @Operation(summary = "设置签名")
    @GetMapping("/setSigner")
    public void setSigner(@RequestParam String signer) {
        String encrypt = aesHelper.encrypt(signer);
        Config config = configService.getByCategoryAndKey(CacheKeyEnum.CONFIG.getCode(), "signer");
        Config update = new Config();
        update.setId(config.getId());
        update.setConfigValue(encrypt);
        configService.updateById(update);
        // 刷新config缓存
        SpringUtil.publishEvent(new RefreshConfigEvent(this));
    }

    @Operation(summary = "重建所有缓存")
    @GetMapping("/refreshConfig")
    public void refreshConfig() {
        SpringUtil.publishEvent(new ReBuildCacheEvent(this));
    }

    @Operation(summary = "更新能量池")
    @GetMapping("/refreshEnergyPool")
    public void refreshEnergyPool() {
        SpringUtil.publishEvent(new ReFreshEnergyPoolEvent(this));
    }



    @Operation(summary = "sse推送测试")
    @GetMapping("/sseSend")
    public Long sseSend(@RequestParam Long uid,
                        @RequestParam(required = false) String name,
                        @RequestParam String msg) {
        SseNameEnum nameEnum = SseNameEnum.of(name);
        return sseService.sendTo(uid, nameEnum == null ? null : name, msg);
    }

    @Operation(summary = "发送tg提醒")
    @GetMapping("/sendTgNotify")
    public Integer sendTgNotify() {
        TgNotifyDTO tgNotifyDTO = new TgNotifyDTO();
        tgNotifyDTO.setScene("商户提现失败");
        tgNotifyDTO.setOriented("dev");
        SpringUtil.publishEvent(new TgNotifyEvent(this, tgNotifyDTO));
        return 1;
    }

    @Operation(summary = "查看App配置 ")
    @GetMapping("/getAppProp")
    public AppProperties getAppProp() {
        return appProperties;
    }

    @Operation(summary = "设置App檲 ")
    @GetMapping("/setAppProp")
    public AppProperties setAppProp(AppProperties prop) {
        BeanCopior.copy(prop, appProperties);
        SpringUtil.publishEvent(new PropRefreshEvent(this));
        return appProperties;
    }
}

