package com.cs.copy.chain.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author gpthk
 * @since 2024-10-24
 */
@Getter
@Setter
@TableName("c_chain_tx_flow")
@Schema(name = "ChainTxFlow", description = "")
public class ChainTxFlow implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "扫描任务id")
    private Long scanId;

    @Schema(description = "区块号")
    private Long blockNo;

    @Schema(description = "区块时间")
    private Long blockTime;

    @Schema(description = "区块hash")
    private String blockHash;

    @Schema(description = "事务hash")
    private String hash;

    @Schema(description = "序列号")
    private Long nonce;

    @Schema(description = "发送地址")
    private String fromAddr;

    @Schema(description = "接收地址")
    private String toAddr;

    @Schema(description = "智能合约地址")
    private String contract;

    @Schema(description = "转账数量")
    private BigDecimal value;

    @Schema(description = "区块信息")
    private String input;

    @Schema(description = "备注")
    private String memo;

    @Schema(description = "记账状态，1=是，0=否")
    private Byte receiptStatus;

    @Schema(description = "方法id")
    private String methodId;
    @Schema(description = "queryid")
    private String queryId;

    @Schema(description = "0=待处理，1=已处理，2=处理出错")
    private Byte status;

    @Schema(description = "错误次数")
    private Integer errorNum;

    @Schema(description = "业务处理失败信息")
    private String errorMsg;

    @Schema(description = "延迟确认时间")
    private Long confirmAt;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "更新时间")
    private Date updateTime;

    @TableLogic(delval = "id")
    private Long deleted;
}
