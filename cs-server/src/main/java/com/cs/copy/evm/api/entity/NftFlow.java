package com.cs.copy.evm.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * @since 2024-12-29
 */
@Getter
@Setter
@TableName("c_nft_flow")
@Schema(name = "NftFlow", description = "")
public class NftFlow implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long blockNo;

    private Integer logIndex;

    private Integer txIndex;

    private String contractAddr;

    private String fromAddr;

    private String toAddr;

    private Long tokenId;

    private String hash;

    private Date createTime;
}
