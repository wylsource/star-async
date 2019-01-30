package com.starylwu.starasync.future;

import java.util.concurrent.Executor;
import java.util.function.Function;

/**
 * @author wuyulong
 * @date 2019/1/30
 * @desc 扩展的Apply
 */
public final class UniApply<T, V> extends AbstractExtendCompletion<T, V> {

    private Function<? super T, ? extends V> function;

    public UniApply(Executor executor, JobFuture<T> src, JobFuture<V> dep, Function<? super T, ? extends V> function) {
        super(executor, src, dep);
        this.function = function;
    }

    @Override
    JobFuture<?> tryFire(int mode) {
        return null;
    }
}
