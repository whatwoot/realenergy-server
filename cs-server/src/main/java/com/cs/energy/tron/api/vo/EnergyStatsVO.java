package com.cs.energy.tron.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "能量统计信息")
public class EnergyStatsVO {

    @Schema(description = "转账地址")
    private String depositAddress;

    @Schema(description = "交易量")
    private Long transactionVolume;

    @Schema(description = "池子总量")
    private Long energyPoolSize;

    @Schema(description = "总用户数")
    private Long userCount;

    @Schema(description = "交易量")
    private Long availableEnergy;
}
