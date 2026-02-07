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
import com.cs.copy.evm.api.event.RefreshAddrBalanceEvent;
import com.cs.copy.evm.api.service.EvmService;
import com.cs.copy.evm.api.service.SymbolService;
import com.cs.copy.evm.server.config.prop.EvmProperties;
import com.cs.copy.evm.server.util.EvmUtil;
import com.cs.copy.global.constants.CacheKey;
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
    private MemberService memberService;

    @Autowired
    private AssetService assetService;

    @Autowired
    private SseService sseService;

    @Autowired
    private AssetFlowService assetFlowService;

    @Autowired
    private AesHelper aesHelper;
    @Autowired
    private ChainAddressService chainAddressService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private HashidsHelper hashidsHelper;

    @Autowired
    private EvmProperties evmProperties;

    @Autowired
    private EvmService evmService;

    @Autowired
    private RsaHelper rsaHelper;

    @Autowired
    private MemberWalletService memberWalletService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private SymbolService symbolService;

    @Autowired
    private WithdrawFlowService withdrawFlowService;


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

    @Operation(summary = "更新邀请码")
    @GetMapping("/refreshInviteCode")
    public Long refreshInviteCode(Integer force) {
        List<Member> list = memberService.list();
        Member update;
        long count = 0;
        for (Member member : list) {
            if (YesNoIntEnum.YES.eq(force) || !StringUtils.hasText(member.getInviteCode())) {
                update = new Member();
                update.setId(member.getId());
                update.setInviteCode(hashidsHelper.encode(update.getId()));
                memberService.updateById(update);
                count++;
            }
        }
        return count;
    }

    @Operation(summary = "添加资产")
    @GetMapping("/addAsset")
    public void addAsset(@RequestParam(required = false) Long uid,
                         @RequestParam(required = false) String mainAccount,
                         @RequestParam(defaultValue = "CFST") String symbol,
                         @RequestParam(defaultValue = "0101") String scene,
                         @RequestParam BigDecimal amount,
                         @RequestParam(required = false) String memo) {
        Member member;
        if (uid == null) {
            member = memberService.getOne(new QueryWrapper<Member>().lambda().eq(Member::getMainAccount, mainAccount));
            expectNotNull(member, "chk.common.invalid", "mainAccount");
        } else {
            member = memberService.getById(uid);
            expectNotNull(member, "chk.common.invalid", "id");
        }
        AssetFlow assetFlow = new AssetFlow();
        assetFlow.setType(AssetTypeEnum.DEFAULT.getCode());
        assetFlow.setUid(member.getId());
        assetFlow.setSymbol(symbol);
        assetFlow.setBalance(amount);
        assetFlow.setMemo(memo);
        AssetSceneEnum sceneEnum = AssetSceneEnum.of(scene);
        expectNotNull(sceneEnum, "chk.common.invalid", "type");
        assetFlow.setScene(sceneEnum.getCode());
        switch (sceneEnum) {
            case WITHDRAW:
            case RECHARGE:
                MemberWallet one = memberWalletService.getOne(new QueryWrapper<MemberWallet>().lambda()
                        .eq(MemberWallet::getUid, assetFlow.getUid())
                        .eq(MemberWallet::getType, MemberWalletTypeEnum.RECHARGE.getCode()));
                if (one != null) {
                    assetFlow.setMemo(StringUtil.senseWallet(one.getWallet()));
                }
                break;
        }
        assetService.updateAsset(assetFlow);
        sseService.sendTo(member.getId(), SseNameEnum.ADD_ASSET.getCode(), member.getId());
    }

    @Operation(summary = "设置归集地址")
    @GetMapping("/setCollectAddr")
    public ChainAddress setCollectAddr(@RequestParam String wallet,
                                       @RequestParam(defaultValue = "USDT") String symbol,
                                       @RequestParam(required = false, defaultValue = "0") Integer weight) {
        expect(WalletUtils.isValidAddress(wallet), "chk.common.invalid", "wallet");
        ChainAddress addr = new ChainAddress();
        addr.setChain(ChainEnum.BSC.getCode());
        addr.setType(AddressTypeEnum.COLLECT.getCode());
        addr.setSymbol(symbol);
        // 充值地址不需要私钥，其他需要私钥
        addr.setNeedRefresh(YesNoByteEnum.YES.getCode());
        addr.setAddr(wallet.toLowerCase());
        addr.setShowAddr(Keys.toChecksumAddress(wallet));
        addr.setStatus(YesNoByteEnum.YES.getCode());
        addr.setUpdateAt(0L);
        addr.setWeight(weight);
        chainAddressService.save(addr);
        return addr;
    }

    @Operation(summary = "刷新钱包余额")
    @GetMapping("/refreshAddrBalance")
    public void refreshAddrBalance() {
        SpringUtil.publishEvent(new RefreshAddrBalanceEvent(this));
    }

    @Operation(summary = "初始化指定数量的钱包")
    @GetMapping("/initWallet")
    public ChainAddress initTonWallet(@RequestParam(defaultValue = "bsc") String chain,
                              @RequestParam(defaultValue = "1", required = false) Integer num,
                              @RequestParam(defaultValue = "CFST") String symbol,
                              @RequestParam(defaultValue = "1", required = false) String type,
                              @RequestParam(required = false) String kp,
                              @RequestParam(required = false, defaultValue = "false") Boolean samePrivate,
                              @RequestParam(required = false, defaultValue = "0") Integer weight,
                              @RequestParam(required = false, defaultValue = "0") String status
                            ) throws CipherException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, IOException {
        // 如果用私钥，就只生成一个钱包地址
        Credentials credentials;
        if (StringUtils.hasText(kp)) {
            credentials = Credentials.create(kp);
        } else {
            String keystoreFileName = WalletUtils.generateFullNewWalletFile(evmProperties.getKeyPwd(), new File(evmProperties.getKeystorePath()));
            File keystore = new File(evmProperties.getKeystorePath(), keystoreFileName);
            credentials = WalletUtils.loadCredentials(evmProperties.getKeyPwd(), keystore);
        }
        String priv = Numeric.toHexStringNoPrefix(credentials.getEcKeyPair().getPrivateKey());
        ChainAddress addr = new ChainAddress();
        addr.setChain(ChainEnum.BSC.getCode());
        addr.setType(Byte.valueOf(type));
        addr.setSymbol(symbol);
        // 充值地址不需要私钥，其他需要私钥
        addr.setNeedRefresh(AddressTypeEnum.RECHARGE.eq(addr.getType()) ? YesNoByteEnum.NO.getCode() : YesNoByteEnum.YES.getCode());
        addr.setAddr(credentials.getAddress());
        addr.setShowAddr(Keys.toChecksumAddress(credentials.getAddress()));
        addr.setPrivKey(aesHelper.encrypt(priv));
        addr.setStatus(Byte.valueOf(status));
        addr.setUpdateAt(0L);
        chainAddressService.save(addr);
        return addr;
    }

    @Operation(summary = "发送钱包中的代币")
    @GetMapping("/sendBscCoin")
    public String sendCoin(@RequestParam(required = false) Long id,
                           @RequestParam(required = false) String addr,
                           @RequestParam String to,
                           @RequestParam(defaultValue = "USDT") String symbol,
                           @RequestParam(required = false, defaultValue = "") String contract,
                           @RequestParam BigDecimal amount) throws IOException {
        ChainAddress chainAddress = null;
        if (id != null) {
            chainAddress = chainAddressService.getById(id);
        } else if (StringUtils.hasText(addr)) {
            chainAddress = chainAddressService.getOne(Wrappers.lambdaQuery(ChainAddress.class)
                    .eq(ChainAddress::getChain, ChainEnum.BSC.getCode())
                    .eq(ChainAddress::getAddr, addr));
        } else {
            throwParamException("chk.common.required", "addr");
        }
        expectNotNull(chainAddress, "chk.common.invalid");
        expectNotBlank(chainAddress.getPrivKey(), "chk.common.invalid", "privateKey");
        String priv = aesHelper.decrypt(chainAddress.getPrivKey());
        Integer decimals = 18;
        if (!StringUtils.hasText(contract)) {
            expectNotBlank(symbol, "chk.common.required", "symbol or contract");
            Symbol coin = symbolService.getOne(new QueryWrapper<Symbol>().lambda()
                    .eq(Symbol::getChain, ChainEnum.BSC.getCode())
                    .eq(Symbol::getSymbol, symbol));
            contract = coin.getQuoteCa();
            decimals = coin.getQuoteDecimals();
            expectNotBlank(contract, "chk.common.invalid", "contract");
        }
        Pair<Response.Error, EthSendTransaction> pair = evmService.broadcast(priv, contract, "transfer", Arrays.asList(new Address(to), new Uint256(EvmUtil.toWei(amount, decimals))), Collections.emptyList());
        if (pair.getLeft() != null) {
            return pair.getLeft().getCode() + ":" + pair.getLeft().getMessage();
        }
        return pair.getRight().getTransactionHash();
    }

    @Operation(summary = "approve授权")
    @GetMapping("/approve")
    public String approve(@RequestParam(required = false) Long id,
                          @RequestParam(required = false) String addr,
                          @RequestParam String contract,
                          @RequestParam String spender,
                          @RequestParam(required = false) String amount) throws IOException {
        ChainAddress chainAddress = null;
        if (id != null) {
            chainAddress = chainAddressService.getById(id);
        } else if (StringUtils.hasText(addr)) {
            chainAddress = chainAddressService.getOne(new QueryWrapper<ChainAddress>().lambda().eq(ChainAddress::getAddr, addr));
        } else {
            throwParamException("chk.common.required", "addr");
        }
        expectNotNull(chainAddress, "chk.common.invalid");
        String priv = aesHelper.decrypt(chainAddress.getPrivKey());
        BigInteger num;
        if (StringUtils.hasText(amount)) {
            num = new BigInteger(amount);
        } else {
            num = BigInteger.valueOf(2).pow(256).subtract(BigInteger.ONE);
        }
        Pair<Response.Error, EthSendTransaction> pair = evmService.broadcast(priv, contract, "approve", Arrays.asList(new Address(spender), new Uint256(num)), Collections.emptyList());
        return pair.getLeft() != null ? pair.getLeft().getMessage() : pair.getRight().getTransactionHash();
    }

    @Operation(summary = "补充用户充值地址")
    @GetMapping("/ensureRechargeAddr")
    public Integer testEnsureRechargeAddr(@Valid EnsureRechargeAddrRequest req) {
        return memberService.resetRechargeAddr(req.getForce(), req.getUid(), req.getSymbol(), req.getChain());
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

