package com.starylwu.starasync.pool;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @Auther: Wuyulong
 * @Date: 2019/1/26 11:11
 * @Description: 用于操作任务的核心接口
 */
interface Job<T>{

    
    <U> Job<U> thenApplyAsync(Function<? super T, ? extends U> fn) ;

    Job<Void> thenAcceptAsync(Consumer<? super T> action) ;

    Job<Void> thenRunAsync(Runnable action) ;

    <U, V> Job<V> thenCombineAsync(Job<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn) ;

    <U> Job<Void> thenAcceptBothAsync(Job<? extends U> other, BiConsumer<? super T, ? super U> action) ;

    Job<Void> runAfterBothAsync(Job<?> other, Runnable action) ;

    <U> Job<U> applyToEitherAsync(Job<? extends T> other, Function<? super T, U> fn) ;

    Job<Void> acceptEitherAsync(Job<? extends T> other, Consumer<? super T> action) ;

    Job<Void> runAfterEitherAsync(Job<?> other, Runnable action) ;

    <U> Job<U> thenComposeAsync(Function<? super T, ? extends Job<U>> fn) ;

    Job<T> exceptionally(Function<Throwable, ? extends T> fn) ;

    Job<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action) ;

    <U> Job<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn) ;

    JobFuture<T> toJobFuture() ;

    CompletableFuture<T> getCompletableFuture();
}
