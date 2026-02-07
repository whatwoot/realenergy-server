package com.cs.web.spring.redismq.base;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.cs.web.spring.redismq.RedisMqProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.cs.sp.common.WebAssert.isNotBlank;

/**
 * @authro fun
 * @date 2025/5/23 19:00
 */
@Slf4j
public abstract class BaseConsumer<T> {
    protected final RedisTemplate redisTemplate;
    protected final RedisMqProperties config;
    private ExecutorService executor;
    public static final String RETRY_KEY = ":retry";
    public static final String DLQ_KEY = ":dlq";

    public BaseConsumer(RedisTemplate redisTemplate, RedisMqProperties config) {
        this.redisTemplate = redisTemplate;
        this.config = config;
        initConsumer();
    }

    @PostConstruct
    public void start() {
        if(StringUtils.hasText(config.getStreamKey()) && StringUtils.hasText(config.getConsumer().getName())){
            executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>(), new BasicThreadFactory.Builder()
                    .namingPattern("rs-consume-%d").daemon(true).build(), new ThreadPoolExecutor.AbortPolicy());
            executor.submit(this::consumeLoop);
        }
    }

    private void initConsumer() {
        isNotBlank(config.getStreamKey(), "chk.common.required", "streamKey");
        isNotBlank(config.getConsumer().getName(), "chk.common.required", "consumer.name");
        try {
            redisTemplate.opsForStream().createGroup(config.getStreamKey(), config.getConsumer().getGroup());
        } catch (RedisSystemException e) {
            log.info("Rs-create-group exists {}/{}", config.getConsumer().getGroup(), config.getStreamKey());
        }
    }

    private void consumeLoop() {
        String consumerId = config.getConsumer().getGroup() + "-" + config.getConsumer().getName();
        AtomicInteger counter = new AtomicInteger(1);
        while (!Thread.currentThread().isInterrupted()) {
            try {
                handler(consumerId);
                // 成功就重置
                counter.set(1);
            }catch (RedisSystemException e){
                log.warn("Rs-consume stop {}", e.getMessage());
                Thread.currentThread().interrupt();
            } catch (Throwable e) {
                log.warn(StrUtil.format("Rs-fail no.{} of {}", counter.get(), consumerId), e);
                errorHandler(consumerId, counter, e);
            }
        }
    }


    /**
     * 1、覆盖这个方法可以重写全部的处理
     *
     * @param consumerId
     */
    protected void handler(String consumerId) {
        defaultHandler(consumerId);
    }

    /**
     * 全局异常处理
     * 用于redis、数据库连接异常或其他场景下非业务的异常
     *
     * @param consumerId
     * @param count
     */
    protected void errorHandler(String consumerId, AtomicInteger count, Throwable e) {
        safeSleep(config.getConsumer().getRetryBaseMs() << count.get());
    }

    /**
     * 默认的处理
     * 1、拉取pending的列表
     * 2、拉取新业务列表
     * 3、异常时，利用 pending 去重试，等待短时间后，重新进pending
     * 4、死信。重试3次进死信
     *
     * @param consumerId
     */
    protected void defaultHandler(String consumerId) {
        // 1. 优先处理Pending消息
        List<MapRecord<String, String, String>> pendingRecords = fetchPending(consumerId);
        if (pendingRecords != null && !pendingRecords.isEmpty()) {
            processPending(pendingRecords);
            return;
        }
        // 2. 长轮询新消息（阻塞式）
        List<MapRecord<String, String, String>> newRecords = fetchNew(consumerId);
        if (newRecords != null && !newRecords.isEmpty()) {
            processNew(newRecords);
        }
    }

    @Nullable
    private List fetchNew(String consumerId) {
        return redisTemplate.opsForStream().read(
                Consumer.from(config.getConsumer().getGroup(), consumerId),
                StreamReadOptions.empty().block(config.getConsumer().getBlockDuration()),
                StreamOffset.create(config.getStreamKey(), ReadOffset.lastConsumed())
        );
    }

    protected List<MapRecord<String, String, String>> fetchPending(String consumerId) {
        // 1. 查询Pending消息列表
        PendingMessages pending = redisTemplate.opsForStream().pending(config.getStreamKey(),
                Consumer.from(config.getConsumer().getGroup(), consumerId),
                Range.unbounded(), config.getConsumer().getBatchSize());

        if (pending.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 获取Pending消息的详细内容
        // 2. 计算最小和最大ID（假设ID是连续的）
        String minId = pending.get(0).getId().getValue();
        String maxId = pending.get(pending.size() - 1).getId().getValue();

        // 3. 单次范围查询（Spring Data Redis 封装）
        return redisTemplate.opsForStream().range(config.getStreamKey(),
                Range.from(Range.Bound.inclusive(minId)).to(Range.Bound.inclusive(maxId)),
                RedisZSetCommands.Limit.limit().count(config.getConsumer().getBatchSize().intValue())
        );
    }

    protected void processNew(List<MapRecord<String, String, String>> newRecords) {
        process(newRecords);
    }

    protected void processPending(List<MapRecord<String, String, String>> pendingRecords) {
        process(pendingRecords);
    }

    protected void process(List<MapRecord<String, String, String>> records) {
        for (MapRecord<String, String, String> record : records) {
            try {
                // 1. 业务处理
                // 这里也可以自己调用方法删除，需要需要事务的时候，返回false即可
                if (process(record)) {
                    ack(record);
                    safeClearRetry(record);
                }
            } catch (Throwable e) {
                processErrorHandler(record, e);
            }
        }
    }

    /**
     * 业务处理异常方法
     * 覆盖可以自行实现
     *
     * @param record
     * @param e
     */
    protected void processErrorHandler(MapRecord<String, String, String> record, Throwable e) {
        defaultProcessErrorHandler(record, e);
    }

    protected void defaultProcessErrorHandler(MapRecord<String, String, String> record, Throwable e) {
        try {
            // 处理失败的场景有没有成功
            boolean done = processFail(record, e);
            if(!done){
                // 等待指定时间
                safeSleep(config.getConsumer().getRetryBaseMs());
            }
        } catch (Throwable ex) {
            // 最终降级
            log.error(StrUtil.format("Rs-consume-retry-fail: {}", record.getId()), ex);
            moveToDeadLetterQueue(record);
        }
    }

    protected boolean processFail(MapRecord<String, String, String> record, Throwable e) {
        // 1. 获取当前重试次数
        String retryKey = config.getStreamKey() + RETRY_KEY;
        Long retryCount = redisTemplate.opsForHash().increment(retryKey, record.getId().getValue(), 1);
        if (retryCount == null) retryCount = 1L;
        // 2. 判断是否超过最大重试次数
        if (retryCount > config.getConsumer().getMaxRetries()) {
            log.error("Rs-consume-moveToDlq-max-retry: {}", record.getId());
            moveToDeadLetterQueue(record);
            safeClearRetry(record);
            return true;
        }
        // 利用pending重试
        if(e != null){
            log.warn(StrUtil.format("Rs-consume-fail-retry: no.{} of {}", retryCount, record.getId()), e);
        }else{
            log.warn("Rs-consume-fail-retry: no.{} of {}", retryCount, record.getId());
        }
        return false;
    }

    protected void safeClearRetry(MapRecord<String, String, String> record) {
        try {
            String retryKey = config.getStreamKey() + RETRY_KEY;
            redisTemplate.opsForHash().delete(retryKey, record.getId().getValue());
        } catch (Throwable e) {
            log.warn("Rs-consume-del-retry-fail: {}", record.getId());
        }
    }

    /**
     * 覆盖默认的可以修改该事件
     *
     * @param record
     */
    protected void moveToDeadLetterQueue(MapRecord<String, String, String> record) {
        defaultMoveToDeadLetterQueue(record);
    }

    protected void defaultMoveToDeadLetterQueue(MapRecord<String, String, String> record) {
        try {
            redisTemplate.opsForList().rightPush(config.getStreamKey() + DLQ_KEY, JSONObject.from(record.getValue()).toJSONString());
            ack(record);
            log.error("Rs-consume-moveToDlq: {}", record.getId());
        } catch (Throwable e) {
            log.error(StrUtil.format("Rs-consume-moveToDlq-fail: {}", record.getId()), e);
        }
    }


    /**
     * 做业务事务
     *
     * @param record
     * @return 返回true, 会自动帮做ack
     * 返回false,如果考虑事务或其他情况想自己做ack时
     * @return
     */
    protected abstract boolean process(MapRecord<String, String, String> record);

    protected abstract Class<T> getDataType();

    protected T parseData(MapRecord<String, String, String> record) {
        return JSONObject.from(record.getValue()).toJavaObject(getDataType());
    }

    protected void ack(MapRecord<String, String, String> record) {
        redisTemplate.opsForStream().acknowledge(config.getStreamKey(), config.getConsumer().getGroup(), record.getId());
    }

    public void safeSleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
        }
    }
}
