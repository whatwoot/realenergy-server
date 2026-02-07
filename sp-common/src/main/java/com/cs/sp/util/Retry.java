package com.cs.sp.util;

import com.cs.sp.common.CommonException;
import com.cs.sp.common.WebAssert;
import com.cs.sp.common.WrapperException;

import java.util.concurrent.Callable;

/**
 * 简单的重试方法
 * @author sb
 * @date 2023/5/27 17:05
 */
public class Retry {

    @FunctionalInterface
    public interface Condition {
        /**
         * 你可根据异常类型决定是否需要重试，
         * @param e 会传入实际报错的信息
         * @return
         */
        boolean ifTry(Exception e);
    }

    /**
     * 有返回值
     * 异常时即重试
     * @param callable
     * @param num
     * @param <T>
     * @return
     */
    public static <T> T retry(Callable<T> callable, int num) {
        return retry(callable, null, num);
    }

    /**
     * 有返回值
     * 根据给定的条件重试
     * @param callable
     * @param num
     * @param <T>
     * @return
     */
    public static <T> T retry(Callable<T> callable, Condition when, int num) {
        WebAssert.expectGt0(num, "sp.retry.numBigThan0");
        Exception exception = null;
        for (int i = 0; i < num; i++) {
            try {
                return callable.call();
            } catch (Exception e) {
                exception = e;
                // 如果有重试条件，并且重试结果是false
                if(when != null && !when.ifTry(e)){
                    break;
                }
            }
        }
        throw Retry.dealException(exception);
    }

    /**
     * 无返回值
     * 异常时即重试
     * @param runnable
     * @param num
     */
    public static void retry(Runnable runnable, int num) {
        retry(runnable, null, num);
    }

    /**
     * 无返回值
     * 根据条件重试
     * @param runnable
     * @param when
     * @param num
     */
    public static void retry(Runnable runnable, Condition when, int num) {
        WebAssert.expectGt0(num, "sp.retry.numBigThan0");
        Exception exception = null;
        for (int i = 0; i < num; i++) {
            try {
                runnable.run();
                return;
            } catch (Exception e) {
                exception = e;
                // 如果有重试条件，并且重试结果是false
                if(when != null && !when.ifTry(e)){
                    break;
                }
            }
        }
        throw Retry.dealException(exception);
    }

    public static RuntimeException dealException(Exception exception) {
        WebAssert.expectNotNull(exception, "sp.retry.errIsNull");
        if (exception instanceof CommonException) {
            CommonException serviceException = (CommonException) exception;
            return serviceException;
        }
        return new WrapperException(exception, exception != null ? exception.getMessage() : null);
    }
}

