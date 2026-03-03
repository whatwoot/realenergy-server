package com.cs.energy.chain.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cs.energy.chain.api.entity.ChainAddress;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author gpthk
 * @since 2024-11-23
 */
@Mapper
public interface ChainAddressMapper extends BaseMapper<ChainAddress> {

    /**
     * @param type
     * @param updateAt
     * @param expireAt
     * @return
     */
    List<ChainAddress> selectNeedRefresh(@Param("type") Byte type,
                                         @Param("chain") String chain,
                                         @Param("updateAt") Long updateAt,
                                         @Param("expireAt") Long expireAt
                                        );
}
