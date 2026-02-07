package com.cs.copy.chain.server.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cs.copy.chain.api.entity.ChainAddress;
import com.cs.copy.chain.api.enums.AddressTypeEnum;
import com.cs.copy.chain.api.service.ChainAddressService;
import com.cs.copy.chain.server.mapper.ChainAddressMapper;
import com.cs.copy.evm.server.config.prop.EvmProperties;
import com.cs.copy.global.constants.CacheKey;
import com.cs.copy.global.constants.Gkey;
import com.cs.copy.member.api.entity.Member;
import com.cs.copy.member.api.entity.MemberWallet;
import com.cs.copy.member.api.enums.ChainEnum;
import com.cs.copy.member.api.enums.MemberWalletTypeEnum;
import com.cs.copy.member.api.event.AddWalletEvent;
import com.cs.copy.member.server.mapper.MemberMapper;
import com.cs.copy.member.server.mapper.MemberWalletMapper;
import com.cs.copy.system.api.event.ReBuildCacheEvent;
import com.cs.copy.system.api.service.ConfigService;
import com.cs.sp.enums.YesNoByteEnum;
import com.cs.web.spring.helper.aeshelper.AesHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Keys;
import org.web3j.crypto.WalletUtils;
import org.web3j.utils.Numeric;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.cs.sp.common.WebAssert.throwBizException;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author gpthk
 * @since 2024-11-23
 */
@Slf4j
@Service
public class ChainAddressServiceImpl extends ServiceImpl<ChainAddressMapper, ChainAddress> implements ChainAddressService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Autowired
    private ConfigService configService;

    @Autowired
    private AesHelper aesHelper;
    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private EvmProperties evmProperties;

    @Autowired
    private MemberWalletMapper memberWalletMapper;

    /**
     * 根据刷新事件，刷新缓存
     *
     * @param e
     */
    @EventListener
    @Async
    public void init(ReBuildCacheEvent e) {

    }

    @Override
    public List<ChainAddress> listRechargeAndCached(Byte type, String chain, boolean force) {
        String key = String.format("%s:%s:%s", CacheKey.CHAIN_ADDR_TYPE, chain, type);
        if (!force) {
            String json = stringRedisTemplate.opsForValue().get(key);
            if (StringUtils.hasText(json)) {
                return JSONArray.parseArray(json, ChainAddress.class);
            }
        }
        List<ChainAddress> list = list(new QueryWrapper<ChainAddress>().lambda()
                .eq(ChainAddress::getType, type)
                .eq(ChainAddress::getChain, chain)
                .eq(ChainAddress::getStatus, YesNoByteEnum.YES.getCode())
        );
        stringRedisTemplate.opsForValue().set(key, JSONArray.toJSONString(list));
        return list;
    }

    @Override
    public List<ChainAddress> listRechargeAndCached(Byte type, String chain) {
        return listRechargeAndCached(type, chain, false);
    }

    @Override
    public Map<String, ChainAddress> listAsMap(Byte type, String chain) {
        List<ChainAddress> chainAddresses = listRechargeAndCached(type, chain);
        return chainAddresses.stream()
                .collect(Collectors.toMap(ChainAddress::getAddr, address -> address));
    }

    @Override
    public void markNeedRefresh(ChainAddress addr) {
        ChainAddress update = new ChainAddress();
        update.setId(addr.getId());
        update.setNeedRefresh(YesNoByteEnum.YES.getCode());
        update.setUpdateAt(System.currentTimeMillis() + Gkey.TON_WAIT);
        updateById(update);
    }

    @Override
    public void addNewWallet(Member member) {
        ChainAddress addr = genWallet();
        // 更新充值钱包信息
        MemberWallet wallet = new MemberWallet();
        wallet.setUid(member.getId());
        wallet.setChain(ChainEnum.BSC.getCode());
        wallet.setType(MemberWalletTypeEnum.RECHARGE.getCode());
        wallet.setWallet(addr.getAddr());
        wallet.setCreateAt(System.currentTimeMillis());
        memberWalletMapper.insert(wallet);
        SpringUtil.publishEvent(new AddWalletEvent(this, wallet));
    }

    @Override
    public ChainAddress genWallet() {
        try {
            String keystoreFileName = WalletUtils.generateFullNewWalletFile(evmProperties.getKeyPwd(), new File(evmProperties.getKeystorePath()));
            File keystore = new File(evmProperties.getKeystorePath(), keystoreFileName);
            Credentials credentials = WalletUtils.loadCredentials(evmProperties.getKeyPwd(), keystore);
            String priv = Numeric.toHexStringNoPrefix(credentials.getEcKeyPair().getPrivateKey());
            ChainAddress addr = new ChainAddress();
            addr.setChain(ChainEnum.BSC.getCode());
            addr.setType(AddressTypeEnum.RECHARGE.getCode());
            // 充值地址不需要私钥，其他需要私钥
            addr.setNeedRefresh(YesNoByteEnum.NO.getCode());
            addr.setSymbol(Gkey.USDT);
            addr.setAddr(credentials.getAddress());
            addr.setShowAddr(Keys.toChecksumAddress(credentials.getAddress()));
            addr.setPrivKey(aesHelper.encrypt(priv));
            addr.setStatus(YesNoByteEnum.YES.getCode());
            addr.setUpdateAt(0L);
            save(addr);
            return addr;
        } catch (Exception e) {
            log.error("Gen-wallet failed", e);
            throwBizException("chk.wallet.genFailed");
        }
        return null;
    }
}
