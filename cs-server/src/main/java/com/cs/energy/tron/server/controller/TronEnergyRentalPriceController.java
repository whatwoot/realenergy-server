package com.cs.energy.tron.server.controller;

import com.cs.energy.tron.api.vo.EnergyStatsVO;
import com.cs.energy.tron.service.EnergyAccountService;
import com.cs.energy.tron.service.EnergyRentalService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * Tron能量租赁价格表 前端控制器
 * </p>
 *
 * @author gpthk
 * @since 2026-01-31
 */
@RestController
@RequestMapping("/api/tron")
public class TronEnergyRentalPriceController {
    @Autowired
    private EnergyRentalService energyRentalService;

    @Autowired
    private EnergyAccountService energyAccountService;

    @Operation(summary = "状态数据")
    @GetMapping("/stats")
    public EnergyStatsVO stats() {
        return energyAccountService.getEnergyStatsVO();
    }
}
