package com.cs.energy.evm.server.service.impl;

import cn.hutool.core.util.StrUtil;
import com.cs.energy.evm.api.service.EvmService;
import com.cs.energy.evm.server.factory.EvmFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Keys;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.RpcErrors;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthEstimateGas;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.ExecutionException;

import static org.web3j.protocol.core.Response.Error;

/**
 * @author fiona
 * @date 2024/5/29 20:00
 */
@Service
@Slf4j
public class EvmServiceImpl implements EvmService {

    private EvmFactory evmFactory;

    private static final Error NET_ERR = new Error(RpcErrors.INTERNAL_ERROR, "network error");
    public static final BigDecimal GWEI = Convert.toWei(BigDecimal.ONE, Convert.Unit.GWEI);

    public EvmServiceImpl(EvmFactory evmFactory) {
        this.evmFactory = evmFactory;
    }

    @Override
    public EvmFactory factory() {
        return evmFactory;
    }

    @Override
    public Web3j web3j() {
        return evmFactory.defaultChain().get();
    }

    @Override
    public Web3j web3j(String chain) {
        return evmFactory.getEvm(chain).get();
    }

    @Override
    public Long chainId() {
        return evmFactory.defaultChain().id();
    }

    @Override
    public Long chainId(String chain) {
        return evmFactory.getEvm(chain).id();
    }

    @Override
    public Pair<Error, EthSendTransaction> transfer(String priv, String to, BigDecimal amount) throws IOException {
        return transfer(Credentials.create(priv), to, amount);
    }

    @Override
    public Pair<Error, EthSendTransaction> transfer(Credentials credentials, String to, BigDecimal amount) throws IOException {
        return transfer(web3j(), credentials, to, amount);
    }

    @Override
    public Pair<Error, EthSendTransaction> transfer(String chain, String priv, String to, BigDecimal amount) throws IOException {
        return transfer(chain, Credentials.create(priv), to, amount);
    }

    @Override
    public Pair<Error, EthSendTransaction> transfer(String chain, Credentials credentials, String to, BigDecimal amount) throws IOException {
        return transfer(web3j(chain), credentials, to, amount);
    }

    @Override
    public Pair<Error, EthSendTransaction> transfer(Web3j web3j, Credentials credentials, String to, BigDecimal amount) throws IOException {
        String from = String.format("0x%s", Keys.getAddress(credentials.getEcKeyPair()));
        log.info("{} => {}【{}】", from, to, amount);
        BigInteger nonce = web3j.ethGetTransactionCount(from, DefaultBlockParameterName.PENDING).send().getTransactionCount();
        BigInteger gasPrice = web3j.ethGasPrice().send().getGasPrice();
        BigInteger gasLimit = BigInteger.valueOf(21000);

        BigDecimal wei = Convert.toWei(amount, Convert.Unit.ETHER);
        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, to, wei.toBigInteger(), "");
        byte[] signMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        //广播交易
        EthSendTransaction tx = null;
        try {
            tx = web3j.ethSendRawTransaction(Numeric.toHexString(signMessage)).sendAsync().get();
            Error error = tx.getError();
            if (tx.getError() != null) {
                log.warn("{} transfer failed: {}", from, error);
                return Pair.of(error, null);
            }
            return Pair.of(null, tx);
        } catch (InterruptedException | ExecutionException e) {
            log.warn(StrUtil.format("{} transfer failed: {}", from, e.getMessage()), e);
        }
        return Pair.of(NET_ERR, null);
    }

    @Override
    public Pair<Error, EthSendTransaction> broadcast(String chain, String priv, String to, String funcName, List<Type> inputs,
                                                     List<TypeReference<?>> outputs) throws IOException {
        return broadcast(chain, Credentials.create(priv), to, funcName, inputs, outputs);
    }

    @Override
    public Pair<Error, EthSendTransaction> broadcast(String chain, Credentials credentials, String to, String funcName, List<Type> inputs,
                                                     List<TypeReference<?>> outputs) throws IOException {
        return broadcast(web3j(chain), credentials, to, funcName, inputs, outputs);
    }

    @Override
    public Pair<Error, EthSendTransaction> broadcast(String priv, String to, String funcName, List<Type> inputs,
                                                     List<TypeReference<?>> outputs) throws IOException {
        return broadcast(Credentials.create(priv), to, funcName, inputs, outputs);
    }

    @Override
    public Pair<Error, EthSendTransaction> broadcast(Credentials credentials, String to, String funcName, List<Type> inputs,
                                                     List<TypeReference<?>> outputs) throws IOException {
        return broadcast(web3j(), credentials, to, funcName, inputs, outputs);
    }

    @Override
    public Pair<Error, EthSendTransaction> broadcast(Web3j web3j, Credentials credentials, String to, String funcName, List<Type> inputs,
                                                     List<TypeReference<?>> outputs) throws IOException {
        String from = credentials.getAddress();
        BigInteger nonce = web3j.ethGetTransactionCount(from, DefaultBlockParameterName.PENDING).send().getTransactionCount();
        StringJoiner sj = new StringJoiner(",");
        inputs.stream().forEach(item -> sj.add(item.getValue().toString()));
        log.info("{} nonce {}, {}({})", from, nonce, funcName, sj.toString());
        Function function = new Function(funcName, inputs, outputs);
        String data = FunctionEncoder.encode(function);
        Transaction gasLimitTx = Transaction.createEthCallTransaction(from, to, data);

        EthEstimateGas ethEstimateGas = web3j.ethEstimateGas(gasLimitTx).send();
        Error error = ethEstimateGas.getError();
        if (error != null) {
            log.warn("estimateGas error: {} {}", error.getCode(), error.getMessage());
            return Pair.of(error, null);
        }

        BigInteger gasLimit = ethEstimateGas.getAmountUsed();

        BigInteger gasPrice = web3j.ethGasPrice().send().getGasPrice();
//        BigInteger newPrice = gasPrice.divide(BigInteger.valueOf(2));
//        // 不让低于1Gwei
//        gasPrice = newPrice.compareTo(GWEI.toBigInteger()) < 0 ? GWEI.toBigInteger() : newPrice;
        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, to, data);
        byte[] signMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        //广播交易
        EthSendTransaction tx = null;
        try {
            tx = web3j.ethSendRawTransaction(Numeric.toHexString(signMessage)).sendAsync().get();
            Error err = tx.getError();
            if (err != null) {
                log.warn(StrUtil.format("{} broadcast failed {}: {}", from, err.getCode(), err.getMessage()), err);
                return Pair.of(err, null);
            }
            log.info("{} broadcast ok {}, gas {} price {}", from, tx.getTransactionHash(), gasLimit, gasPrice);
            return Pair.of(null, tx);
        } catch (InterruptedException | ExecutionException e) {
            log.warn(StrUtil.format("{} broadcast failed {}", from, e.getMessage()), e);
        }
        // 3000000000
        // 10000000000
        return Pair.of(NET_ERR, null);
    }

    @Override
    public Pair<Error, List<Type>> ethCall(String from, String to, String funcName, List<Type> inputs,
                                           List<TypeReference<?>> outputs) throws IOException {
        return ethCall(web3j(), from, to, funcName, inputs, outputs);
    }

    @Override
    public Pair<Error, List<Type>> ethCall(String chain, String from, String to, String funcName, List<Type> inputs,
                                           List<TypeReference<?>> outputs) throws IOException {
        return ethCall(web3j(chain), from, to, funcName, inputs, outputs);
    }

    private Pair<Error, List<Type>> ethCall(Web3j web3j, String from, String to, String funcName, List<Type> inputs,
                                            List<TypeReference<?>> outputs) throws IOException {
        Function function = new Function(funcName, inputs, outputs);
        String data = FunctionEncoder.encode(function);
        Transaction tx = Transaction.createEthCallTransaction(from, to, data);
        EthCall ethCall = web3j.ethCall(tx, DefaultBlockParameterName.PENDING).send();
        Error error = ethCall.getError();
        if (error == null) {
            String str = ethCall.getResult();
            List<Type> decodeList = FunctionReturnDecoder.decode(str, function.getOutputParameters());
            return Pair.of(null, decodeList);
        }
        return Pair.of(error, null);
    }
}
