package com.cs.copy.asset.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cs.copy.asset.api.entity.WithdrawFlow;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * <p>
 * 提现流水 Mapper 接口
 * </p>
 *
 * @author gpthk
 * @since 2024-10-03
 */
@Mapper
public interface WithdrawFlowMapper extends BaseMapper<WithdrawFlow> {

    BigDecimal sumTotal(Integer ymd, Collection<String> scenes);

    int updateClear(Integer ymd);
}
