package com.cs.energy.system.api.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.Serializable;
import java.util.Set;

/**
 * @author fiona
 * @date 2024/7/14 23:31
 */
public interface SseService {
    SseEmitter addNew(String sid, Long id);

    Set<Long> onlineUids();

    long sendTo(Long uid, Serializable msg);

    long sendTo(Long uid, String name, Serializable msg);

    long sendTo(Long uid, String id, String name, Serializable msg);

    long sendTo(Set<Long> uids, String name, Serializable msg);


    long sendAll(Serializable msg);

    long sendAll(String name, Serializable msg);
}
