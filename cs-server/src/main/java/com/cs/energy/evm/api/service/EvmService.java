package com.cs.energy.evm.api.service;

import com.cs.energy.evm.server.factory.EvmFactory;
import org.apache.commons.lang3.tuple.Pair;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Type;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthSendTransaction;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.web3j.protocol.core.Response.Error;

/**
 * @author fiona
 * @date 2024/5/29 20:00
 */
public interface EvmService {

    EvmFactory factory();

    Long chainId();

    Long chainId(String chain);

    Web3j web3j();

    Web3j web3j(String chain);

    /**
     * 转账公链币
     *
     * @param priv
     * @param to
     * @param amount
     * @return
     */
    Pair<Error, EthSendTransaction> transfer(String priv, String to, BigDecimal amount) throws IOException;
    Pair<Error, EthSendTransaction> transfer(Credentials credentials, String to, BigDecimal amount) throws IOException;
    Pair<Error, EthSendTransaction> transfer(String chain, String priv, String to, BigDecimal amount) throws IOException;
    Pair<Error, EthSendTransaction> transfer(String chain, Credentials credentials, String to, BigDecimal amount) throws IOException;
    Pair<Error, EthSendTransaction> transfer(Web3j web3j, Credentials credentials, String to, BigDecimal amount) throws IOException;

    /**
     * 默认节点广播
     *
     * @param priv
     * @param to
     * @param funcName
     * @param inputs
     * @param outputs
     * @return
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    Pair<Error, EthSendTransaction> broadcast(String priv, String to, String funcName, List<Type> inputs, List<TypeReference<?>> outputs) throws IOException;
    Pair<Error, EthSendTransaction> broadcast(Credentials credentials, String to, String funcName, List<Type> inputs, List<TypeReference<?>> outputs) throws IOException;

    /**
     * 指定链广播
     *
     * @param chain
     * @param priv
     * @param to
     * @param funcName
     * @param inputs
     * @param outputs
     * @return
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    Pair<Error, EthSendTransaction> broadcast(String chain, String priv, String to, String funcName, List<Type> inputs, List<TypeReference<?>> outputs) throws IOException;
    Pair<Error, EthSendTransaction> broadcast(String chain, Credentials credentials, String to, String funcName, List<Type> inputs, List<TypeReference<?>> outputs) throws IOException;
    Pair<Error, EthSendTransaction> broadcast(Web3j web3j, Credentials credentials, String to, String funcName, List<Type> inputs, List<TypeReference<?>> outputs) throws IOException;

    /**
     * 默认链上方法调用
     *
     * @param from
     * @param to
     * @param funcName
     * @param inputs
     * @param outputs
     * @return
     * @throws IOException
     */
    Pair<Error, List<Type>> ethCall(String from, String to, String funcName, List<Type> inputs, List<TypeReference<?>> outputs) throws IOException;

    /**
     * 指定链上方法调用
     *
     * @param chain
     * @param from
     * @param to
     * @param funcName
     * @param inputs
     * @param outputs
     * @return
     * @throws IOException
     */
    Pair<Error, List<Type>> ethCall(String chain, String from, String to, String funcName, List<Type> inputs, List<TypeReference<?>> outputs) throws IOException;
}
