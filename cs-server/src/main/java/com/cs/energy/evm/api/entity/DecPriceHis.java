package com.cs.energy.evm.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author gpthk
 * @since 2026-01-04
 */
@Getter
@Setter
@TableName("c_dec_price_his")
@Schema(name = "DecPriceHis", description = "")
public class DecPriceHis implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String symbol;

    private String poolAddr;

    @Schema(description = "token0数量")
    private BigInteger reserve0;

    @Schema(description = "token1数量")
    private BigInteger reserve1;
    private Integer decimals0;
    private Integer decimals1;

    @Schema(description = "价格时间")
    private Long priceTime;

    @Schema(description = "价格")
    private BigDecimal price;

    @Schema(description = "价格方向，0=token0价格,1=token1价格")
    private Integer priceDirection;

    private Long blockNo;

    @Schema(description = "有效。1=有效,0=无效")
    private Byte status;

    private Long updateAt;

    private Date createTime;
}
