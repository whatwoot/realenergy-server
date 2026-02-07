package com.cs.copy.tron.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 能量池账号实体
 * 存储提供能量的账号信息
 */
@Data
@TableName("tron_energy_account")
public class EnergyAccount implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * Tron账号地址 (Base58格式)
     */
    private String address;

    private String agent;

    /**
     * 账号私钥 (加密存储)
     */
    private String privateKey;

    /**
     * 总能量
     */
    private Long totalEnergy;

    /**
     * 可用能量
     */
    private Long availableEnergy;

    private Long rentEnergy;

    /**
     * 状态: 1-活跃, 0-禁用
     */
    private Integer status;

    /**
     * 创建时间戳
     */
    private Long createAt;

    /**
     * 更新时间戳
     */
    private Long updateAt;
}
