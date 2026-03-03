package com.cs.energy.evm.server.factory;

import cn.hutool.core.lang.Assert;
import com.cs.energy.evm.api.enums.EvmEnum;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fiona
 * @date 2024/5/29 19:31
 */
public class EvmFactory {

    private String defaults;
    private ConcurrentHashMap<String, EvmChain> chainMap;

    private EvmFactory() {
        chainMap = new ConcurrentHashMap<>();
    }

    public static EvmFactory defaults() {
        return new EvmFactory();
    }

    public EvmFactory add(String chain, EvmChain evmEngine) {
        if (!chainMap.containsKey(chain)) {
            chainMap.put(chain, evmEngine);
        }
        return this;
    }

    public EvmFactory add(EvmEnum chain, EvmChain evmEngine) {
        if (!chainMap.containsKey(chain.getChain())) {
            chainMap.put(chain.getChain(), evmEngine);
        }
        return this;
    }

    public boolean containsEvm(String chain) {
        return chainMap != null && chainMap.containsKey(chain);
    }

    public boolean containsEvm(EvmEnum chain) {
        return chainMap != null && chainMap.containsKey(chain.getChain());
    }

    public EvmChain defaultChain() {
        return chainMap.get(defaults);
    }

    /**
     * 设置默认链
     * @param chain
     */
    public EvmFactory asDefault(String chain){
        EvmChain evmChain = chainMap.get(chain);
        Assert.notNull(evmChain);
        defaults = chain;
        return this;
    }

    public EvmChain getEvm(String chain) {
        return chainMap.get(chain);
    }

    public EvmChain getEvm(EvmEnum chain) {
        return chainMap.get(chain.getChain());
    }

    public ConcurrentHashMap<String, EvmChain> getEvmList() {
        return chainMap;
    }

}
