package com.starylwu.starasync.rewrite;

import java.util.concurrent.*;
import java.util.function.*;

/**
 * @Auther: Wuyulong
 * @Date: 2019/1/26 11:05
 * @Description: 用于操作任务的核心实现
 */
public class JobFuture<T> implements Future<T>, Job<T> {

    private CompletableFuture<T> completableFuture;

    private Executor executor;

    public JobFuture(CompletableFuture<T> completableFuture, Executor executor) {
        this.completableFuture = completableFuture;
        this.executor = executor;
    }

    //------重写future 方法 --start
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return completableFuture.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return completableFuture.isCancelled();
    }

    @Override
    public boolean isDone() {
        return completableFuture.isDone();
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        return completableFuture.get();
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return completableFuture.get(timeout, unit);
    }

    //------重写future 方法 --end

    //------重写Job 方法 --start
    public static <U> JobFuture<U> supplyAsync(Supplier<U> supplier, Executor executor) {
        return new JobFuture<>(CompletableFuture.supplyAsync(supplier, executor), executor);
    }

    @Override
    public <U> JobFuture<U> thenApplyAsync(Function<? super T, ? extends U> fn) {
        return new JobFuture<>(this.completableFuture.thenApplyAsync(fn, this.executor), this.executor);
    }

    @Override
    public Job<Void> thenAcceptAsync(Consumer<? super T> action) {
        return new JobFuture<>(completableFuture.thenAcceptAsync(action, this.executor), this.executor);
    }

    @Override
    public Job<Void> thenRunAsync(Runnable action) {
        return new JobFuture<>(completableFuture.thenRunAsync(action, this.executor), this.executor);
    }

    @Override
    public <U, V> Job<V> thenCombineAsync(Job<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn) {
        return new JobFuture<>(completableFuture.thenCombineAsync(other.getCompletableFuture(), fn, this.executor), this.executor);
    }

    @Override
    public <U> Job<Void> thenAcceptBothAsync(Job<? extends U> other, BiConsumer<? super T, ? super U> action) {
        return new JobFuture<>(completableFuture.thenAcceptBothAsync(other.getCompletableFuture(), action, this.executor), this.executor);
    }

    @Override
    public Job<Void> runAfterBothAsync(Job<?> other, Runnable action) {
        return new JobFuture<>(completableFuture.runAfterBothAsync(other.getCompletableFuture(), action, this.executor), this.executor);
    }

    @Override
    public <U> Job<U> applyToEitherAsync(Job<? extends T> other, Function<? super T, U> fn) {
        return new JobFuture<>(completableFuture.applyToEitherAsync(other.getCompletableFuture(), fn, this.executor), this.executor);
    }

    @Override
    public Job<Void> acceptEitherAsync(Job<? extends T> other, Consumer<? super T> action) {
        return new JobFuture<>(completableFuture.acceptEitherAsync(other.getCompletableFuture(), action, this.executor), this.executor);
    }

    @Override
    public Job<Void> runAfterEitherAsync(Job<?> other, Runnable action) {
        return new JobFuture<>(completableFuture.runAfterEitherAsync(other.getCompletableFuture(), action, this.executor), this.executor);
    }

    @Override
    public <U> Job<U> thenComposeAsync(Function<? super T, ? extends Job<U>> fn) {
        throw new UnsupportedOperationException("not support method");
    }

    @Override
    public Job<T> exceptionally(Function<Throwable, ? extends T> fn) {
        return new JobFuture<>(completableFuture.exceptionally(fn), this.executor);
    }

    @Override
    public Job<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action) {
        return new JobFuture<>(completableFuture.whenCompleteAsync(action, this.executor), this.executor);
    }

    @Override
    public <U> Job<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn) {
        return new JobFuture<>(completableFuture.handleAsync(fn, this.executor), this.executor);
    }

    @Override
    public JobFuture<T> toJobFuture() {
        return this;
    }

    @Override
    public CompletableFuture<T> getCompletableFuture() {
        return this.completableFuture;
    }


    //------重写Job 方法 --end


    @Override
    public String toString() {
        return "JobFuture{" +
                "completableFuture=" + completableFuture +
                ", executor=" + executor +
                '}';
    }
}
