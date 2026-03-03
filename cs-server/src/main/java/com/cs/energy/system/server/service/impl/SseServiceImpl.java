package com.cs.energy.system.server.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson2.JSONObject;
import com.cs.energy.system.api.event.SseConnectEvent;
import com.cs.energy.system.api.service.SseService;
import com.cs.web.spring.helper.RedisIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author fiona
 * @date 2024/7/15 18:08
 */
@Service
@Slf4j
public class SseServiceImpl implements SseService {

    private static final long SSE_TIMEOUT = 30 * 60 * 1000;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    public static final Map<String, SseEmitter> pool = new ConcurrentHashMap<>();
    // 因为SseEmitter在重启后是无效的，所以sid没有必要存，会断连后触发重连，重新加上的
    public static final Map<String, Long> sidMap = new ConcurrentHashMap<>();
    public static final String SSE_KEY = "g:sse:%s";
    public static final String NAME_BROADCAST = "broadcast";
    public static final String NAME_MSG = "message";
    public static final String ID_KEY = "sse:id";

    public static final Integer THRESHOLD = 500;

    @Autowired
    private RedisIdWorker redisIdWorker;

    @Override
    public Set<Long> onlineUids() {
        return new HashSet<>(sidMap.values());
    }

    @Override
    public SseEmitter addNew(String clientId, Long id) {
        SseEmitter sseEmitter = new SseEmitter(SSE_TIMEOUT);
        if (id == null) {
            try {
                sseEmitter.send(SseEmitter.event().reconnectTime(3000)
                        .name("401").data(0));
            } catch (IOException e) {
            }
            return sseEmitter;
        }
        pool.put(clientId, sseEmitter);
        sidMap.put(clientId, id);
        stringRedisTemplate.opsForSet().add(String.format(SSE_KEY, id), clientId);
        // 连接断开回调
        sseEmitter.onCompletion(() -> {
            log.info("SSE-connect-complete {}, {}", id, clientId);
            removeFromPool(clientId, "complete");
        });
        // 连接超时
        sseEmitter.onTimeout(() -> {
            log.info("SSE-connect-timeout {}, {}", id, clientId);
            // 由complete去关闭
            sseEmitter.complete();
        });
        // 连接报错
        sseEmitter.onError((throwable) -> {
            log.info("SSE-connect-error {}, {}", id, clientId);
            // 由complete去关闭
            sseEmitter.complete();
        });
        try {
            sseEmitter.send(SseEmitter.event().reconnectTime(3000).name("ping").data(id));
            SpringUtil.publishEvent(new SseConnectEvent(this, id, clientId));
        } catch (IOException e) {
            log.warn("ping fail", e);
        }
        return sseEmitter;
    }

    private void removeFromPool(String clientId, String reason) {
        try {
            SseEmitter sse = pool.get(clientId);
            pool.remove(clientId);
            Long uid = sidMap.remove(clientId);
            long removed = 0;
            if (uid != null) {
                removed = stringRedisTemplate.boundSetOps(String.format(SSE_KEY, uid)).remove(clientId);
            }
            if (sse != null) {
                sse.complete();
            }
            log.info("SSE remove {}:{} {}, By {}", reason, uid, removed, clientId);
        } catch (Throwable e) {
            log.info("SSE-removeFromPool failed", e);
        }
    }

    @Override
    public long sendTo(Long uid, Serializable msg) {
        return sendTo(uid, null, msg);
    }

    @Override
    public long sendTo(Long uid, String name, Serializable msg) {
        return sendTo(uid, null, name, msg);
    }

    @Override
    public long sendTo(Set<Long> uids, String name, Serializable msg) {
        Set<String> collect = uids.stream().map(uid -> {
            BoundSetOperations<String, String> ops = stringRedisTemplate.boundSetOps(String.format(SSE_KEY, uid));
            return ops.members();
        }).flatMap(Set::stream).collect(Collectors.toSet());
        return asyncSendAndSum(collect, null, name, msg);
    }

    @Override
    public long sendTo(Long uid, String id, String name, Serializable msg) {
        HashSet<Long> ids = new HashSet<>();
        ids.add(uid);
        return sendTo(ids, name, msg);
    }

    private Long asyncSendAndSum(Set<String> members, String id, String name, Serializable msg) {
        // 将数组元素转为 CompletableFuture
        List<CompletableFuture<Long>> futures = members.stream()
                .map(sid -> CompletableFuture.supplyAsync(() -> processItem(pool.get(sid), id, name, msg))
                        .exceptionally(e -> 0L))
                .collect(Collectors.toList());

        // 等待所有任务完成并收集结果
        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        return allOf.thenApply(v ->
                futures.stream()
                        .mapToLong(CompletableFuture::join) // 使用 join 获取结果并转为 long 基本类型
                        .sum() // 求和
        ).exceptionally(e -> 0L).join();
    }

    private Long processItem(SseEmitter sseEmitter, String id, String name, Serializable msg) {
        String message;
        if (msg instanceof String) {
            message = (String) msg;
        } else {
            message = JSONObject.toJSONString(msg);
        }
        if (sseEmitter != null) {
            try {
                // 我们没有保存消息的机制，所以没办法重连后根据lastId补发，故不使用id
                sseEmitter.send(SseEmitter.event()
//                        .id(id == null ? String.valueOf(redisIdWorker.nextId(ID_KEY)) : id)
                        .name(name == null ? NAME_MSG : name)
                        .data(message, MediaType.APPLICATION_JSON));
                return 1L;
            } catch (Throwable e) {
                log.warn("SSE failed {} {}={}", e.getClass(), name, message);
            }
        }
        return 0L;
    }


    @Override
    public long sendAll(Serializable msg) {
        return sendAll(NAME_BROADCAST, msg);
    }

    @Override
    public long sendAll(String name, Serializable msg) {
        List<SseEmitter> list = new ArrayList<>(pool.values());
        int size = list.size();

        int numBatches = (int) Math.ceil((double) size / THRESHOLD);
        List<List<SseEmitter>> batches = new ArrayList<>();

        // 使用 IntStream 来生成每个批次的起始索引并分割数组
        IntStream.range(0, numBatches).forEach(i -> {
            int start = i * THRESHOLD;
            int end = Math.min(start + THRESHOLD, size);
            batches.add(list.subList(start, end));
        });
        return batches.stream().mapToLong(item -> pressBatch(item, name, msg)).sum();
    }

    private Long pressBatch(List<SseEmitter> item, String name, Serializable msg) {
        List<CompletableFuture<Long>> futures = item.stream()
                .map(batch -> CompletableFuture.supplyAsync(() -> processItem(batch, null, name, msg))
                        .exceptionally(e -> 0L))
                .collect(Collectors.toList());

        // 等待所有任务完成，并统计成功的数量
        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        return allOf.thenApply(v ->
                futures.stream()
                        .mapToLong(CompletableFuture::join) // 获取每个异步任务的结果
                        .sum()
        ).join();
    }
}
