package com.cs.copy.evm.api.dto;

import com.cs.web.redis.base.RedisObj;

/**
 * @author fiona
 * @date 2024/5/30 18:27
 */
public class EvmScanBlock implements RedisObj {

    public final static String KEY = "kisar:evm:scan";

    @Override
    public String key() {
        return KEY;
    }
}
