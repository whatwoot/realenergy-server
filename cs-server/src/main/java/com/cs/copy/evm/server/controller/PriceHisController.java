package com.cs.copy.evm.server.controller;

import com.cs.copy.evm.api.service.PriceHisService;
import com.cs.copy.evm.api.vo.PriceHisVO;
import com.cs.copy.global.constants.CacheKey;
import com.cs.copy.global.constants.Gkey;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author gpthk
 * @since 2024-12-10
 */
@Tag(name = "跟单交易")
@RestController
@RequestMapping("/api/priceHis")
public class PriceHisController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Operation(summary = "实时代币价格")
    @GetMapping("/now")
    public Map<String, String> all() {
        HashOperations<String, String, String> ops = stringRedisTemplate.opsForHash();
        return ops.entries(CacheKey.PRICE_MAP);
    }
}

