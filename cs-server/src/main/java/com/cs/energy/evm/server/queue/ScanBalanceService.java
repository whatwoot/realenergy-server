package com.cs.energy.evm.server.queue;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cs.energy.chain.api.entity.ChainAddress;
import com.cs.energy.chain.api.enums.AddressTypeEnum;
import com.cs.energy.chain.server.mapper.ChainAddressMapper;
import com.cs.energy.evm.api.common.EvmConstant;
import com.cs.energy.evm.api.entity.Symbol;
import com.cs.energy.evm.api.enums.SymbolTypeEnum;
import com.cs.energy.evm.api.event.RefreshAddrBalanceEvent;
import com.cs.energy.evm.api.service.EvmService;
import com.cs.energy.evm.api.service.SymbolService;
import com.cs.energy.evm.server.mapper.SymbolMapper;
import com.cs.energy.evm.server.util.EvmUtil;
import com.cs.energy.member.api.enums.ChainEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.Response;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author fiona
 * @date 2024/12/26 05:31
 */
@Slf4j
@Component
public class ScanBalanceService {

    @Autowired
    private ChainAddressMapper chainAddressMapper;

    @Autowired
    private EvmService evmService;

    @Autowired
    private SymbolMapper symbolMapper;

    @Autowired
    private SymbolService symbolService;

    private Boolean scaning = false;

    @EventListener
    @Async
    public void handleEvent(RefreshAddrBalanceEvent event) {
        scanBalance();
    }

//    @Scheduled(cron = "0 0 * * * ?")
    public void scanBalance() {
        log.info("ScanBalance start {}", scaning);
        if (scaning) {
            return;
        }
        scaning = true;
        try {
            doScan();
        } finally {
            scaning = false;
        }
    }

    private void doScan() {
        List<ChainAddress> chainAddresses = chainAddressMapper.selectList(new QueryWrapper<ChainAddress>().lambda()
                .eq(ChainAddress::getChain, ChainEnum.BSC.getCode())
                .eq(ChainAddress::getType, AddressTypeEnum.RECHARGE.getCode())
        );
        if (chainAddresses.isEmpty()) {
            return;
        }

        Map<String, Symbol> symbolMap = symbolService.listAsTypeChainSymolMap();
        if (symbolMap.isEmpty()) {
            return;
        }

        EthGetBalance send;
        ChainAddress updateBalance;
        Integer num = 0;
        for (ChainAddress chainAddress : chainAddresses) {
            try {
                send = evmService.web3j().ethGetBalance(chainAddress.getAddr(), DefaultBlockParameterName.PENDING).send();
                BigDecimal balance = Convert.fromWei(new BigDecimal(send.getBalance()), Convert.Unit.ETHER);
                updateBalance = new ChainAddress();
                updateBalance.setId(chainAddress.getId());
                updateBalance.setBalance(balance);
//                Symbol usdt = symbolMap.get(chainAddress.getSymbol());
                Symbol usdt = symbolMap.get(StrUtil.join(":", SymbolTypeEnum.WITHDRAWAL.getCode(), ChainEnum.BSC.getCode(), chainAddress.getSymbol()));
                if (usdt != null && StringUtils.hasText(usdt.getBaseCa())) {
                    Pair<Response.Error, List<Type>> pair = evmService.ethCall(chainAddress.getAddr(), usdt.getBaseCa(),
                            EvmConstant.BALANCE_OF, Arrays.asList(new Address(chainAddress.getAddr())
                            ), Arrays.asList(EvmConstant.TYPE_UINT256));
                    if (pair.getValue() != null && !pair.getValue().isEmpty()) {
                        BigInteger usdtBig = ((Uint256) pair.getRight().get(0)).getValue();
                        BigDecimal usdtValue = EvmUtil.fromWei(usdtBig, usdt.getBaseDecimals());
                        updateBalance.setUsdtBalance(usdtValue);
                    }
                }
                chainAddressMapper.updateById(updateBalance);
                num++;
            } catch (Throwable e) {
                log.warn(StrUtil.format("ScanBalance failed: {}", chainAddress.getAddr()), e);
            }
        }
        log.info("ScanBalance num:{}", num);
    }
}
