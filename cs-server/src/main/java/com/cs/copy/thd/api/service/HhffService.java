package com.cs.copy.thd.api.service;


import com.cs.copy.thd.api.request.HhffCallRequest;

/**
 * @authro fun
 * @date 2025/4/1 22:28
 */
public interface HhffService {
    boolean onNotify(HhffCallRequest req);

    boolean onNotify(HhffCallRequest req, boolean notSign);

    void updateToOk(Long id);

    void updateToRefund(Long id);
}
