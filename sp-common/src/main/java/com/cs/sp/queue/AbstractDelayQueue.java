package com.cs.sp.queue;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.*;
import java.util.function.Predicate;

/**
 * @author sb
 * @date 2024/6/22 21:39
 */
@Slf4j
public abstract class AbstractDelayQueue<T> implements DefaultDelayQueue<T> {
    protected static final Integer DEFAULT_MAX_RETRY = 3;
    protected final DelayQueue<DelayedVo<T>> delayQueue = new DelayQueue<>();
    protected ExecutorService executor;


    public void init() {
        executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), new BasicThreadFactory.Builder()
                .namingPattern(ukey() + "-dq-%d").daemon(true).build(), new ThreadPoolExecutor.AbortPolicy());
        executor.submit(this::runTask);
    }

    public void destory() {
        executor.shutdown();
        log.info("DelayQueue {} stop", ukey() == null ? ukey() : this.getClass().getName());
    }

    @Override
    public int maxRetry() {
        return DEFAULT_MAX_RETRY;
    }

    @Override
    public boolean add(DelayedVo<T> data) {
        return queue().add(data);
    }

    @Override
    public boolean add(T data, long timestamp) {
        return add(new DelayedVo<>(data, timestamp));
    }

    @Override
    public boolean remove(Predicate<T> predicate) {
        return queue().removeIf(delayedVo -> predicate.test(delayedVo.getData()));
    }

    /**
     * 返回true会阻止默认事件，也就会阻止反复加入队列
     *
     * @param e
     * @return
     */
    @Override
    public boolean onFail(Exception e) {
        log.info(StrUtil.format("{} dequeue fail", ukey()), e);
        return true;
    }

    @Override
    public DelayQueue<DelayedVo<T>> queue() {
        return delayQueue;
    }
}
