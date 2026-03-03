package com.cs.energy.system.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cs.energy.system.api.entity.ApplyFlow;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author gpthk
 * @since 2025-10-09
 */
public interface ApplyFlowService extends IService<ApplyFlow> {

    void updateAudit(ApplyFlow req);
}
