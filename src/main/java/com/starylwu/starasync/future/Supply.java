package com.starylwu.starasync.future;

import java.util.concurrent.ForkJoinTask;
import java.util.function.Supplier;

/**
 * @author wuyulong
 * @date 2019/1/29
 * @desc 线程池需要执行的任务扩展(没有入参)
 */
class Supply<T> extends ForkJoinTask<Void> implements Runnable, AsynchronousCompletionJob {

    /**
     * 依赖的JobFuture
     */
    private JobFuture<T> depFuture;

    /**
     * 要执行的任务
     */
    private Supplier<T> job;

    public Supply(JobFuture<T> depFuture, Supplier<T> job) {
        this.depFuture = depFuture;
        this.job = job;
    }

    @Override
    public void run() {
        JobFuture<T> future;
        Supplier<T> executeJob;
        if ((future = depFuture) != null && (executeJob = job) != null){
            //将类属性值转换为局部方法变量，并将属性置为null,是因为需要根据属性值判断这个任务是否做过，防止重复，
            depFuture = null;
            job = null;
            if (future.result == null){
                //任务还没有执行,没有结果
                try {
                    future.setResult(executeJob.get());
                } catch (Throwable throwable){
                    future.setExResult(throwable);
                }
            }
            future.postComplete();
        }
    }

    @Override
    public Void getRawResult() {
        return null;
    }

    @Override
    protected void setRawResult(Void value) {

    }

    /**
     * 如果是forkjointask类型,在执行的时候，会调用exec,而不是run。所以我们在这里调用run
     * @return
     */
    @Override
    protected boolean exec() {
        run();
        return true;
    }
}
