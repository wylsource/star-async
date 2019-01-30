package com.starylwu.starasync.pool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author wuyulong
 * @date 2019/1/30
 * @desc 自定义线程池,因为我们的使用场景，必须使用默认的拒绝策略。
 */
public class ParentThreadPool extends ThreadPoolExecutor {

    private BlockingQueue<Runnable> workQueue;

    public ParentThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, new MainThreadRunHander());
        this.workQueue = workQueue;
    }


    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
    }

    /**
     * 获取任务队列中的任务数量
     * @return
     */
    public int getWorkQueueSize(){
        return workQueue.size();
    }

    static class MainThreadRunHander implements RejectedExecutionHandler{
        private static AtomicLong rejectedCount = new AtomicLong();

        /**
         * 直接使用当前线程run即可。不能丢弃
         * @param r
         * @param executor
         */
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            long count = rejectedCount.incrementAndGet();
            if (!executor.isShutdown()){
                r.run();
            }
        }
    }

    public long getRejectedCount(){
        return MainThreadRunHander.rejectedCount.get();
    }

}
