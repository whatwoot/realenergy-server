package com.cs.energy.system.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cs.energy.system.api.entity.Notice;
import com.cs.energy.system.api.service.NoticeService;
import com.cs.energy.system.server.mapper.NoticeMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 通知公告表 服务实现类
 * </p>
 *
 * @author gpthk
 * @since 2024-09-04
 */
@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {

}
