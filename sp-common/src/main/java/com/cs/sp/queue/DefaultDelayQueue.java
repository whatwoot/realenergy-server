package com.cs.sp.queue;

import java.util.concurrent.DelayQueue;
import java.util.function.Predicate;

/**
 * @author sb
 * @date 2024/6/22 21:37
 */
public interface DefaultDelayQueue<T> {
    String ukey();

    int maxRetry();

    boolean add(T data, long timestamp);

    boolean add(DelayedVo<T> data);

    void onDelayed(T data);

    boolean remove(Predicate<T> predicate);

    /**
     * 延迟处理队列
     *
     * @return
     */
    DelayQueue<DelayedVo<T>> queue();

    boolean onFail(Exception e);

    /**
     * 处理扫块结果
     */
    default void runTask() {
        DelayedVo<T> take = null;
        int retryNum = 0;
        while (!Thread.currentThread().isInterrupted()) {
            try {
                take = null;
                take = queue().take();
                if (take != null) {
                    onDelayed(take.getData());
                    retryNum = 0;
                }
            } catch (Exception e) {
                if (onFail(e)) {
                    if (take != null) {
                        if (retryNum++ < maxRetry()) {
                            queue().add(take);
                        }
                    }
                }
            }
        }
    }
}
