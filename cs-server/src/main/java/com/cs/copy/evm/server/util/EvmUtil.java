package com.cs.copy.evm.server.util;

import com.cs.copy.evm.api.common.EvmConstant;
import lombok.extern.slf4j.Slf4j;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.Utils;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

/**
 * @author fiona
 * @date 2024/12/25 00:33
 */
@Slf4j
public class EvmUtil {

    public static final Integer DECIMALS = 18;

    public static BigDecimal fromWei(BigInteger num){
        return new BigDecimal(num).divide(BigDecimal.TEN.pow(DECIMALS), DECIMALS, RoundingMode.FLOOR);
    }

    public static BigDecimal fromWei(BigInteger num, Integer decimals){
        return new BigDecimal(num).divide(BigDecimal.TEN.pow(decimals), decimals, RoundingMode.FLOOR);
    }
    public static BigInteger toWei(BigDecimal num, Integer decimals){
        return num.multiply(BigDecimal.TEN.pow(decimals)).toBigInteger();
    }

    public static String decodeAddr(String addr){
        try{
            List<Type> decoded = FunctionReturnDecoder.decode(addr, Utils.convert(Arrays.asList(
                    EvmConstant.TYPE_ADDRESS
            )));
            return ((Address) decoded.get(0)).getValue();
        }catch (Exception e){
            log.warn("EVM-decode failed {}", addr);
        }
        return null;
    }

    public static BigInteger decodeUint256(String input){
        try{
            List<Type> decoded = FunctionReturnDecoder.decode(input, Utils.convert(Arrays.asList(
                    EvmConstant.TYPE_UINT256
            )));
            return ((Uint256) decoded.get(0)).getValue();
        }catch (Exception e){
            log.warn("EVM-decode failed {}", input);
        }
        return null;
    }
}
