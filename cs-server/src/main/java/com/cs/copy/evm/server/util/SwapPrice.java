package com.cs.copy.evm.server.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

/**
 * @authro fun
 * @date 2026/1/4 18:56
 */
public class SwapPrice {

    // PancakeSwap v2 0.25% 手续费
    private static final BigInteger FEE = BigInteger.valueOf(9975);
    private static final BigInteger FEE_BASE = BigInteger.valueOf(10000);

    public static BigDecimal getPrice(
            BigInteger reserve0,
            BigInteger reserve1,
            int decimals0,
            int decimals1,
            int direction) {

        if (direction == 0) {
            // token0 -> token1
            return calculatePrice(
                    reserve0, reserve1,
                    decimals0, decimals1
            );
        } else {
            // token1 -> token0
            return calculatePrice(
                    reserve1, reserve0,
                    decimals1, decimals0
            );
        }
    }

    /**
     * 核心价格计算逻辑
     */
    private static BigDecimal calculatePrice(
            BigInteger fromReserve,
            BigInteger toReserve,
            int fromDecimals,
            int toDecimals) {

        // 1个fromToken的wei数量
        BigInteger oneFromToken = BigInteger.TEN.pow(fromDecimals);

        // PancakeSwap 实际计算（精确复制合约逻辑）
        BigInteger amountInWithFee = oneFromToken.multiply(FEE);
        BigInteger numerator = amountInWithFee.multiply(toReserve);
        BigInteger denominator = fromReserve.multiply(FEE_BASE).add(amountInWithFee);

        // Solidity 整数除法（向零截断）
        BigInteger toTokenWei = numerator.divide(denominator);

        // 转换为标准单位（保持截断行为）
        return new BigDecimal(toTokenWei)
                .divide(new BigDecimal(BigInteger.TEN.pow(toDecimals)),
                        toDecimals,
                        RoundingMode.DOWN);
    }

}
