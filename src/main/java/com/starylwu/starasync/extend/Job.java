package com.starylwu.starasync.extend;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Supplier;

/**
 * @author wuyulong
 * @date 2019/1/30
 * @desc 工作任务
 */
public class Job<T> extends CompletableFuture<T> {


    public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier,
                                                       Executor executor) {

        ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
        return null;
    }
}
