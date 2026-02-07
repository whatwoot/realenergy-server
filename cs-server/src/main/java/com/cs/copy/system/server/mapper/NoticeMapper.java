package com.cs.copy.system.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cs.copy.system.api.entity.Notice;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 通知公告表 Mapper 接口
 * </p>
 *
 * @author gpthk
 * @since 2024-09-04
 */
@Mapper
public interface NoticeMapper extends BaseMapper<Notice> {

}
