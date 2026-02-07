package com.cs.copy.evm.api.enums;

import lombok.Getter;

/**
 * @author fiona
 * @date 2024/5/29 19:51
 */
@Getter
public enum EvmEnum {
    /**
     * 默认为eth
     */
    DEFAULT("default", 1L),
    ETH("eth", 1L),
    BSC("bsc", 56L),
    BSC_TEST("bsc-test", 97L)
    ;

    private String chain;
    private Long chainId;

    EvmEnum(String chain, Long chainId) {
        this.chain = chain;
        this.chainId = chainId;
    }

    public static EvmEnum resolve(String chain) {
        for (EvmEnum value : values()) {
            if (value.eq(chain)) {
                return value;
            }
        }
        return null;
    }

    public boolean eq(String chain) {
        return this.getChain().equals(chain);
    }
}
