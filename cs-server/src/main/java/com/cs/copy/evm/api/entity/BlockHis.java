package com.cs.copy.evm.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cs.sp.common.base.BaseDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author gpthk
 * @since 2024-12-08
 */
@Getter
@Setter
@TableName("c_block_his")
@Schema(name = "BlockHis", description = "")
public class BlockHis extends BaseDO {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long blockNo;

    private String blockHash;

    private Long blockTime;

    private Byte processed;

    private Date createTime;
    private Date updateTime;
}
