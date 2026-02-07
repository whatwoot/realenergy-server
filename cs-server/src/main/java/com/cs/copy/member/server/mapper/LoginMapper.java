package com.cs.copy.member.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cs.copy.member.api.entity.Login;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author gpthk
 * @since 2025-02-20
 */
@Mapper
public interface LoginMapper extends BaseMapper<Login> {

    int countBinded(Login login);
}
