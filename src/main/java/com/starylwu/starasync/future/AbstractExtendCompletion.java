package com.starylwu.starasync.future;

import java.util.concurrent.Executor;

abstract class AbstractExtendCompletion<T, V> extends AbstractCompletion {
    Executor executor;

    JobFuture<T> src;

    JobFuture<V> dep;

    public AbstractExtendCompletion(Executor executor, JobFuture<T> src, JobFuture<V> dep) {
        this.executor = executor;
        this.src = src;
        this.dep = dep;
    }

    final boolean canWork(){
        Executor e = executor;
        if (compareAndSetForkJoinTaskTag((short)0, (short)1)){
            //设置task的状态
            if (e == null){
                return true;
            }
            //将属性置为null，只执行一次
            executor = null;
            e.execute(this);
        }
        return false;
    }

    @Override
    final boolean isLive() {
        return dep != null;
    }
}