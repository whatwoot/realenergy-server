package com.cs.energy.asset.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cs.energy.asset.api.entity.Asset;
import com.cs.energy.asset.api.entity.AssetFlow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author gpthk
 * @since 2024-09-29
 */
@Mapper
public interface AssetMapper extends BaseMapper<Asset> {

    int updateChange(Asset updateAsset);

    int insertOrUpdate(Asset exists);

    int updateSnap(Integer ymd);

    int updateGenesis(Asset asset);

    List<Asset> sumTotal();

    int updateBatch(String symbol, @Param("ids") Collection<Long> uids, @Param("items") List<AssetFlow> items);

    int updateChangeCanBelowZero(Asset updateAsset);
}
