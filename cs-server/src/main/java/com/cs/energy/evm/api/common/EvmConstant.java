package com.cs.energy.evm.api.common;

import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.*;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * @author fiona
 * @date 2024/3/31 16:52
 */
public interface EvmConstant {
    String EVM_CHAIN_TYPE= "evm";
    String BSC_CHAIN= "bsc";

    TypeReference<Address> TYPE_ADDRESS = new TypeReference<Address>() {
    };
    TypeReference<Utf8String> TYPE_STRING = new TypeReference<Utf8String>() {
    };
    TypeReference<DynamicBytes> TYPE_DYNAMIC_STRING = new TypeReference<DynamicBytes>() {
    };
    TypeReference<Bool> TYPE_BOOl = new TypeReference<Bool>() {
    };
    TypeReference<Uint256> TYPE_UINT256 = new TypeReference<Uint256>() {
    };
    TypeReference<Uint112> TYPE_UINT112 = new TypeReference<Uint112>() {
    };
    TypeReference<Uint80> TYPE_UINT80 = new TypeReference<Uint80>() {
    };
    TypeReference<Uint64> TYPE_UINT64 = new TypeReference<Uint64>() {
    };
    TypeReference<Uint32> TYPE_UINT32 = new TypeReference<Uint32>() {
    };
    Uint256 UINT256_MAX = new Uint256(new BigInteger("115792089237316195423570985008687907853269984665640564039457584007913129639935"));
    String BALANCE_OF = "balanceOf";
    String TRANSFER = "transfer";
    String GET_RESERVES = "getReserves";
    String GET_PRICE0CUMULATIVELAST = "price0CumulativeLast";
    String GET_PRICE1CUMULATIVELAST = "price1CumulativeLast";

    Integer TOKEN_NOT_ENOUGH_CODE = 3;
    String TOKEN_NOT_ENOUGH_MSG = "exceeds balance";
    Integer MAIN_COIN_NOT_ENOUGH_CODE = -32000;
    String MAIN_COIN_NOT_ENOUGH_MSG = "insufficient funds";

    Event TRANSFER_EVENT = new Event("Transfer", Arrays.asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>(true) {}));
}
