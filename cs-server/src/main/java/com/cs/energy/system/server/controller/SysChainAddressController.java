package com.cs.energy.system.server.controller;

import cn.hutool.extra.spring.SpringUtil;
import com.cs.energy.chain.api.entity.ChainAddress;
import com.cs.energy.chain.api.enums.AddressTypeEnum;
import com.cs.energy.chain.api.service.ChainAddressService;
import com.cs.energy.evm.api.event.AfterRefreshConfigEvent;
import com.cs.energy.member.api.enums.ChainEnum;
import com.cs.sp.enums.YesNoByteEnum;
import com.cs.web.spring.helper.aeshelper.AesHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Keys;
import org.web3j.crypto.WalletUtils;

import java.util.ArrayList;
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

