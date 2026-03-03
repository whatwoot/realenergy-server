package com.cs.energy.tron.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cs.energy.tron.entity.EnergyRental;
import org.apache.ibatis.annotations.Mapper;

/**
 * 能量租赁记录Mapper
 */
@Mapper
public interface EnergyRentalMapper extends BaseMapper<EnergyRental> {
}
