package com.cs.energy;

import com.cs.energy.evm.api.service.EvmService;
import com.cs.energy.evm.server.factory.BscChain;
import com.cs.energy.evm.server.factory.EvmFactory;
import com.cs.energy.evm.server.service.impl.EvmServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.core.Response;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;

/**
 * @authro fun
 * @date 2026/1/3 00:43
 */
@Slf4j
public class TestTopup {

    private EvmService evmService;

    @Before
    public void before() {
        EvmFactory defaults = EvmFactory.defaults();
        defaults.add("bsc",
//                        new BscChain(56L, "https://bsc-dataseed1.binance.org/")
                        new BscChain(79L, "https://bsc-testnet-dataseed.bnbchain.org")
                )
                .asDefault("bsc");
        evmService = new EvmServiceImpl(defaults);
    }

    @Test
    public void testApprove() throws IOException {
        String priv = "2241c89293d3c36b507011f8e361e35b3653663b882316fc6268c96c8f321247";
        String token ="0xaB1a4d4f1D656d2450692D237fdD6C7f9146e814";
        String ca ="0x12c47270e29b104cf91c34de6b6fb1e0b0ef1c1e";
        Pair<Response.Error, EthSendTransaction> pair = evmService.broadcast(priv, token, "approve", Arrays.asList(
                new Address(ca), new Uint256(BigInteger.valueOf(2)
                        .pow(256)          // 2^256
                        .subtract(BigInteger.ONE))
        ), Collections.emptyList());
        if(pair.getLeft() != null){
            log.info("error {} {}", pair.getLeft().getCode(), pair.getLeft().getMessage());
            return;
        }
        log.info("success {}", pair.getRight().getTransactionHash());
    }

    @Test
    public void test() throws IOException {
        String priv = "2241c89293d3c36b507011f8e361e35b3653663b882316fc6268c96c8f321247";
        String ca ="0x12c47270e29b104cf91c34de6b6fb1e0b0ef1c1e";
        Pair<Response.Error, EthSendTransaction> pair = evmService.broadcast(priv, ca, "topup", Arrays.asList(
            new Uint256(11010),
                new Uint256(Convert.toWei(BigDecimal.valueOf(102), Convert.Unit.ETHER).toBigInteger())
        ), Collections.emptyList());
        if(pair.getLeft() != null){
            log.info("error {} {}", pair.getLeft().getCode(), pair.getLeft().getMessage());
            return;
        }
        log.info("success {}", pair.getRight().getTransactionHash());

    }
}
