package com.cs.energy.evm.api.dto;

import com.cs.web.redis.base.RedisObj;
import lombok.Data;

/**
 * bsc扫块状态和高度
 *
 * @author fiona
 * @date 2024/5/30 19:22
 */
@Data
public class BscChainDTO implements RedisObj {
    public static final String KEY = "kisar:evm:scan:bsc";

    private Long id;
    private Byte status;
    private Long startBlockNo;
    private Long delay;
    private Long maxStep;
    /**
     * 处理到的高度
     */
    private Long blockNo;
    /**
     * 当前扫块高度
     */
    private Long latestBLockNo;
    @Override
    public String key() {
        return KEY;
    }
}
