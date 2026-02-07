package com.cs.copy.evm.server.factory;

import com.cs.copy.evm.api.enums.EvmEnum;
import org.web3j.protocol.Web3j;

/**
 * @author fiona
 * @date 2024/5/29 19:45
 */
public interface EvmChain {
    default Long id(){
        return EvmEnum.ETH.getChainId();
    }
    Web3j get();
}
