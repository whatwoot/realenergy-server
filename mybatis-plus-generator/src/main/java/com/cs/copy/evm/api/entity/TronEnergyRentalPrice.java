package com.cs.copy.evm.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * Tron能量租赁价格表
 * </p>
 *
 * @author gpthk
 * @since 2026-01-31
 */
@Getter
@Setter
@TableName("tron_energy_rental_price")
@Schema(name = "TronEnergyRentalPrice", description = "Tron能量租赁价格表")
public class TronEnergyRentalPrice implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "购买的能量份数")
    private Integer rentPieces;

    @Schema(description = "指定份数能量的trx数目，sun")
    private Long trxAmount;

    @Schema(description = "出租的能量数量")
    private Long energyAmount;

    @Schema(description = "创建时间戳(毫秒)")
    private Long createAt;

    @Schema(description = "更新时间戳(毫秒)")
    private Long updateAt;
}
