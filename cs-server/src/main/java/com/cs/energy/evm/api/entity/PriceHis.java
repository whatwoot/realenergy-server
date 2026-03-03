package com.cs.energy.evm.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cs.sp.common.base.BaseDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author gpthk
 * @since 2024-12-10
 */
@Getter
@Setter
@TableName("c_price_his")
@Schema(name = "PriceHis", description = "")
public class PriceHis extends BaseDO{

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String symbol;

    private BigDecimal price;

    private Long period;

    private BigDecimal price0CumulativeLast;

    private BigDecimal price1CumulativeLast;

    private BigInteger blockTimestampLast;

    private Long updateAt;

    private Long createAt;

    private Date createTime;

}
