package com.cs.copy.evm.server.factory;

import com.cs.copy.evm.server.config.prop.EvmBscProperties;
import lombok.extern.slf4j.Slf4j;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fiona
 * @date 2024/5/29 20:07
 */
@Slf4j
public class BscChain implements EvmChain {

    private EvmBscProperties evmBscProperties;

    private List<Web3j> web3js = new ArrayList<>();

    public BscChain(Long id, String urls) {
        this(new EvmBscProperties(id, urls));
    }

    public BscChain(EvmBscProperties evmBscProperties) {
        this.evmBscProperties = evmBscProperties;
        this.init();
    }

    public void init() {
        if (web3js.isEmpty()) {
            String[] rpcs = evmBscProperties.getRpcUrls().split(",");
            Web3j web3j;
            for (String rpc : rpcs) {
                web3j = Web3j.build(new HttpService(rpc));
                web3js.add(web3j);
            }
        }
        if (web3js.isEmpty()) {
            log.warn("no Evm Rpc");
            System.exit(1);
        }
    }

    @Override
    public Web3j get() {
        return web3js.get((int) (Math.random() * web3js.size()));
    }

    @Override
    public Long id() {
        return evmBscProperties.getChainId() == null ? 58L : evmBscProperties.getChainId();
    }
}
