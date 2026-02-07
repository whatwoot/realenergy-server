package com.cs.copy.system.server.controller;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cs.copy.asset.api.entity.AssetFlow;
import com.cs.copy.asset.api.enums.AssetSceneEnum;
import com.cs.copy.asset.api.enums.AssetTypeEnum;
import com.cs.copy.asset.api.service.AssetFlowService;
import com.cs.copy.asset.api.service.AssetService;
import com.cs.copy.asset.api.service.WithdrawFlowService;
import com.cs.copy.chain.api.entity.ChainAddress;
import com.cs.copy.chain.api.enums.AddressTypeEnum;
import com.cs.copy.chain.api.service.ChainAddressService;
import com.cs.copy.evm.api.entity.Symbol;
import com.cs.copy.evm.api.event.AfterRefreshConfigEvent;
import com.cs.copy.evm.api.event.RefreshAddrBalanceEvent;
import com.cs.copy.evm.api.service.EvmService;
import com.cs.copy.evm.api.service.SymbolService;
import com.cs.copy.evm.server.config.prop.EvmProperties;
import com.cs.copy.evm.server.util.EvmUtil;
import com.cs.copy.global.constants.CacheKey;
import com.cs.copy.global.constants.Gkey;
import com.cs.copy.member.api.entity.Member;
import com.cs.copy.member.api.entity.MemberWallet;
import com.cs.copy.member.api.enums.ChainEnum;
import com.cs.copy.member.api.enums.MemberWalletTypeEnum;
import com.cs.copy.member.api.service.LoginService;
import com.cs.copy.member.api.service.MemberService;
import com.cs.copy.member.api.service.MemberWalletService;
import com.cs.copy.system.api.entity.Config;
import com.cs.copy.system.api.enums.CacheKeyEnum;
import com.cs.copy.system.api.enums.SseNameEnum;
import com.cs.copy.system.api.event.PropRefreshEvent;
import com.cs.copy.system.api.event.ReBuildCacheEvent;
import com.cs.copy.system.api.event.RefreshConfigEvent;
import com.cs.copy.system.api.request.EnsureRechargeAddrRequest;
import com.cs.copy.system.api.service.ConfigService;
import com.cs.copy.system.api.service.SseService;
import com.cs.copy.system.server.config.prop.AppProperties;
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
import java.util.ArrayList;
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
@RequestMapping("/sapi/chainAddress")
public class SysChainAddressController {

    @Autowired
    private Environment env;

    @Autowired
    private AesHelper aesHelper;

    @Operation(summary = "充值地址插入")
    @GetMapping("/bsc/addTopupAddr")
    public List<ChainAddress> addTopupAddr(@RequestParam String addr,
                                           @RequestParam(defaultValue = "CFST") String symbol
    ) {
        isTrue(WalletUtils.isValidAddress(addr), "chk.common.invalid");
        Byte type = AddressTypeEnum.RECHARGE.getCode();
        ChainAddress chainAddr = new ChainAddress();
        chainAddr.setChain(ChainEnum.BSC.getCode());
        chainAddr.setType(type);
        chainAddr.setNeedRefresh(YesNoByteEnum.YES.getCode());
        chainAddr.setAddr(addr);
        chainAddr.setShowAddr(Keys.toChecksumAddress(addr));
        chainAddr.setSymbol(symbol);
        chainAddr.setStatus(YesNoByteEnum.YES.getCode());
        chainAddr.setUpdateAt(0L);
        List<ChainAddress> list = new ArrayList<>();
        list.add(chainAddr);
        SpringUtil.getBean(ChainAddressService.class).saveBatch(list);
        // 刷新列表
        SpringUtil.getBean(ChainAddressService.class).listRechargeAndCached(type, "bsc", true);
        // 刷新扫块
        SpringUtil.publishEvent(new AfterRefreshConfigEvent(this));
        return list;
    }

    @Operation(summary = "通过私钥插入提现地址")
    @GetMapping("/bsc/restoreWithdrawAddr")
    public List<ChainAddress> restoreWithdrawAddr(@RequestParam String priv,
                                                  @RequestParam(defaultValue = "CFST") String symbol
                                                  ) {
        Credentials credentials = Credentials.create(priv);
        isTrue(WalletUtils.isValidAddress(credentials.getAddress()), "chk.common.invalid", "priv");
        Byte type = AddressTypeEnum.WITHDRAW.getCode();
        ChainAddress chainAddr = new ChainAddress();
        chainAddr.setChain(ChainEnum.BSC.getCode());
        chainAddr.setType(type);
        chainAddr.setNeedRefresh(YesNoByteEnum.YES.getCode());
        chainAddr.setAddr(credentials.getAddress().toLowerCase());
        chainAddr.setShowAddr(Keys.toChecksumAddress(chainAddr.getAddr()));
        chainAddr.setSymbol(symbol);
        chainAddr.setStatus(YesNoByteEnum.YES.getCode());
        chainAddr.setPrivKey(aesHelper.encrypt(priv));
        chainAddr.setUpdateAt(0L);
        List<ChainAddress> list = new ArrayList<>();
        list.add(chainAddr);
        SpringUtil.getBean(ChainAddressService.class).saveBatch(list);
        SpringUtil.getBean(ChainAddressService.class).listRechargeAndCached(type, "bsc", true);
        return list;
    }
}

