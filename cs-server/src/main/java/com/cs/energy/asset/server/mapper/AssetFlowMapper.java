package com.cs.energy.asset.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cs.energy.asset.api.dto.SceneTotalDTO;
import com.cs.energy.asset.api.dto.SummaryDTO;
import com.cs.energy.asset.api.dto.SummaryTeamDTO;
import com.cs.energy.asset.api.entity.AssetFlow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author gpthk
 * @since 2024-09-29
 */
@Mapper
public interface AssetFlowMapper extends BaseMapper<AssetFlow> {

    SummaryDTO countSummary(@Param("uid") Long id,
                            @Param("day") Integer day,
                            @Param("invalidAt") Long invalidAt,
                            @Param("scenes") List<String> scenes);

    SummaryTeamDTO countTeamSummary(@Param("uid") Long id,
                                    @Param("day") Integer day,
                                    @Param("invalidAt") Long invalidAt,
                                    @Param("scenes") List<String> scenes);

    List<AssetFlow> groupIncome(@Param("symbol") String symbol,
                                @Param("day") Integer day,
                                @Param("scenes") List<String> scenes);

    int updateClear(@Param("symbol") String symbol,
                    @Param("day") Integer day,
                    @Param("scenes") List<String> scenes);

    BigDecimal sumTotal(@Param("type") Byte type,
                        @Param("uid") Long uid,
                        @Param("symbol") String symbol,
                        @Param("day") Integer day,
                        @Param("scenes") List<String> scenes);

    int insertBatch(List<AssetFlow> list);

    List<SceneTotalDTO> groupSceneOf(Integer yest);
}
