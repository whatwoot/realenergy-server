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
 * @since 2024-06-01
 */
@Getter
@Setter
@TableName("c_chain")
@Schema(name = "Chain", description = "")
public class Chain implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "链类型")
    private String chainType;

    @Schema(description = "公链")
    private String chain;

    @Schema(description = "链id")
    private Long chainId;

    @Schema(description = "链名称")
    private String chainName;

    @Schema(description = "rpc地址")
    private String rpcUrls;

    @Schema(description = "扫块延迟")
    private Long delay;

    @Schema(description = "开始区块")
    private Long startBlockNo;

    @Schema(description = "当前区块")
    private Long blockNo;

    @Schema(description = "最大扫块")
    private Long maxStep;
    @Schema(description = "状态。1=扫块,0=不扫块")
    private Byte status;

    @Schema(description = "备注")
    private String memo;
    @Schema(description = "权重")
    private Integer weight;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "更新时间")
    private Date updateTime;

    @Schema(description = "删除标志。0=未删,其他=删除")
    @TableLogic(delval = "id")
    private Long deleted;
}
