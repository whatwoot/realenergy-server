package com.cs.energy.asset.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cs.energy.asset.api.entity.WithdrawDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author gpthk
 * @since 2024-11-25
 */
@Mapper
public interface WithdrawDetailMapper extends BaseMapper<WithdrawDetail> {

    int insertBatch(List<WithdrawDetail> detailList);
}
