package com.cs.copy.evm.server.controller;

import com.cs.copy.evm.server.scan.EvmScanHelper;
import com.cs.sp.constant.Constant;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author gpthk
 * @since 2024-02-08
 */
@Tag(name = "【生产环境不对外】运营接口")
@RestController
@Slf4j
@RequestMapping("/sapi/evm")
public class EvmController {

    @Autowired
    private Environment env;

    @Autowired
    private EvmScanHelper scanHelper;

    @Operation(summary = "开始/停止扫块")
    @GetMapping("/scan/start")
    public void scanStart(Integer flag) {
        if (Constant.ONE_INT.equals(flag)) {
            scanHelper.start();
        } else {
            scanHelper.stop();
        }
    }

    @Operation(summary = "跳至指定区块")
    @GetMapping("/scan/go")
    public void go(@RequestParam Long blockNo) {
        if(blockNo != null && blockNo > 0){
            scanHelper.jumpTo(blockNo);
        }
    }

    @Operation(summary = "扫描指定区块范围")
    @GetMapping("/scan/scan")
    public void scan(@RequestParam Long start, @RequestParam Long end) {
        if(start <= end){
            scanHelper.scan(start, end);
        }
    }

    @Operation(summary = "hash补偿")
    @GetMapping("/scan/hash")
    public Integer scanHash(String tx) throws IOException {
        return 0;
    }
}
