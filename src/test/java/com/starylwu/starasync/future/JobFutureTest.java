package com.starylwu.starasync.future;

import org.junit.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

/**
 * @author wuyulong
 * @date 2019/1/29
 * @desc 测试
 */
public class JobFutureTest {

    private static Executor executor = Executors.newWorkStealingPool(5);
    @Test
    public void asnycSupply() throws ExecutionException, InterruptedException {
        JobFuture.setExecutor(executor);
        Instant start = Instant.now();
        JobFuture<String> supply1 = JobFuture.supply(() -> {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "supply1";
        });

        JobFuture<String> supply2 = JobFuture.supply(() -> {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "supply2";
        });

        JobFuture<String> supply3 = JobFuture.supply(() -> {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "supply3";
        });

        ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();

        System.out.println(supply1.get() + ", " + supply2.get() + ", " + supply3.get() + ", ");
        System.out.println("use time = " + Duration.between(start, Instant.now()).toMillis());
    }
}
