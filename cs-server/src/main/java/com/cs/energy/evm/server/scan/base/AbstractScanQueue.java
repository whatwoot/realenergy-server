package com.cs.energy.evm.server.scan.base;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cs.energy.evm.api.entity.ChainScan;
import com.cs.energy.evm.api.service.ChainScanService;
import com.cs.energy.chain.api.entity.ChainTxFlow;
import com.cs.energy.chain.api.enums.ChainTxFlowStatusEnum;
import com.cs.energy.chain.api.service.ChainTxFlowService;
import com.cs.sp.queue.DelayedVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

/**
 * @author fiona
 * @date 2024/6/4 06:55
 */
@Slf4j
public abstract class AbstractScanQueue implements ScanQueue {

    protected final LinkedBlockingDeque<ChainTxFlow> queue = new LinkedBlockingDeque<>();
    protected final LinkedBlockingDeque<ChainTxFlow> confirmQueue = new LinkedBlockingDeque<>();
    protected final DelayQueue<DelayedVo<ChainTxFlow>> delayQueue = new DelayQueue<>();

    protected ExecutorService executor;
    protected ExecutorService delayExecutor;
    protected ExecutorService confirmExecutor;

    @Override
    public boolean accept(ChainTxFlow txFlow) {
        return false;
    }

    public void doInit() {
        executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), new BasicThreadFactory.Builder()
                .namingPattern(ukey() + "-queue-%d").daemon(true).build(), new ThreadPoolExecutor.AbortPolicy());
        executor.submit(this::runTask);

        delayExecutor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), new BasicThreadFactory.Builder()
                .namingPattern(ukey() + "-delay-%d").daemon(true).build(), new ThreadPoolExecutor.AbortPolicy());
        delayExecutor.submit(this::runDelayCheckTask);

        confirmExecutor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), new BasicThreadFactory.Builder()
                .namingPattern(ukey() + "-delay-%d").daemon(true).build(), new ThreadPoolExecutor.AbortPolicy());
        confirmExecutor.submit(this::runConfirmTask);
    }

    public void doDestory() {
        executor.shutdown();
        delayExecutor.shutdown();
        confirmExecutor.shutdown();
        log.info("DelayQueue {} stop", this.getClass().getName());
    }

    public void checkTxFlow() {
        List<ChainScan> chainScans = SpringUtil.getBean(ChainScanService.class).listBscAndCache(false);
        Optional<ChainScan> first = chainScans.stream().filter(s -> ukey().equals(s.getUkey())).findFirst();
        if (first.isPresent()) {
            ChainScan chainScan = first.get();
            // 找到当前任务，未处理，并且确认时间已经过了当前时间的确认任务，重新发起确认
            List<ChainTxFlow> list = SpringUtil.getBean(ChainTxFlowService.class).list(new QueryWrapper<ChainTxFlow>().lambda()
                    .eq(ChainTxFlow::getScanId, chainScan.getId())
                    .eq(ChainTxFlow::getStatus, ChainTxFlowStatusEnum.NOT_DEAL.getCode())
                    .lt(ChainTxFlow::getErrorNum, maxCheckNum())
                    .le(ChainTxFlow::getConfirmAt, System.currentTimeMillis())
            );
            for (ChainTxFlow txFlow : list) {
                delayQueue.add(new DelayedVo<>(txFlow, txFlow.getConfirmAt()));
            }
        }
    }

    @Override
    public LinkedBlockingDeque<ChainTxFlow> getQueue() {
        return queue;
    }

    @Override
    public LinkedBlockingDeque<ChainTxFlow> getConfirmQueue() {
        return confirmQueue;
    }

    @Override
    public DelayQueue<DelayedVo<ChainTxFlow>> getDelayQueue() {
        return delayQueue;
    }

    @Override
    public void onTaskFail(Exception e) {
        log.error(StrUtil.format("RunTask {} failed", ukey()), e);
    }

    @Override
    public void onDelayTaskFail(Exception e) {
        log.error(StrUtil.format("DelayTask {} failed", ukey()), e);
    }

    @Override
    public void onConfirmFail(Exception e) {
        log.error(StrUtil.format("ConfirmTask {} failed", ukey()), e);
    }

    @Override
    public boolean onCheckReceiptFail(ChainTxFlow take, Exception e) {
        log.warn(StrUtil.format("CheckReceipt Fail: {}", take.getHash()), e);
        return false;
    }
}
