package com.cs.energy.evm.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author gpthk
 * @since 2024-06-03
 */
@Getter
@Setter
@TableName("c_chain_scan")
@Schema(name = "ChainScan", description = "扫块任务")
public class ChainScan implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "公链id")
    private Integer chainId;

    @Schema(description = "链")
    private String chain;

    @Schema(description = "唯一标识")
    private String ukey;

    @Schema(description = "bean名称")
    private String beanName;

    @Schema(description = "扫块方式。1=扫合约,2=nft,3=代币,4=转账")
    private Byte scanType;

    @Schema(description = "方法id")
    private String methodId;

    @Schema(description = "地址")
    private String addr;

    @Schema(description = "合约地址")
    private String contractAddr;

    @Schema(description = "确认区块数量")
    private Long confirmBlockNum;

    @Schema(description = "精度")
    private Integer decimals;

    @Schema(description = "状态。1=有效,0=无效")
    private Byte status;

    @Schema(description = "权重。")
    private Integer weight;

    private Date createTime;

    private Date updateTime;

    @TableLogic(delval = "id")
    private Long deleted;
}
