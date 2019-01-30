package com.starylwu.starasync.completable;

import org.junit.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author wuyulong
 * @date 2019/1/30
 * @desc test
 */
public class FutureTest {

    private static Executor executor = Executors.newWorkStealingPool(Runtime.getRuntime().availableProcessors());

    @Test
    public void testSupplyAndThen() {
        Instant start = Instant.now();

        //第一个任务异步
        CompletableFuture<String> result1 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "future1";
        }, executor).thenApply(s -> {
            //依赖上面的异步任务，在上面异步任务执行完后执行，与上面的异步任务使用同一线程
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return s + " hello";
        });

        //第二个任务异步
        CompletableFuture<String> result2 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "future2";
        }, executor).thenApply(s -> {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return s + " hello";
        });

        //所有任务都执行完
        CompletableFuture<Void> allFuture = CompletableFuture.allOf(result1, result2);

        //任意一个任务执行完
        CompletableFuture<Object> anyFuture = CompletableFuture.anyOf(result1, result2);

        //任务组合并全部执行完成
        CompletableFuture<String> all = result1.thenCombine(result2, (r1, r2) -> r1 + " - " + r2);


        System.out.println(all.join());
        System.out.println("use time = " + Duration.between(start, Instant.now()).toMillis() + " ms");
    }
}
