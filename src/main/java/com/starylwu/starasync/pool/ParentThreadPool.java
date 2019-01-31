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

    public ParentThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, new MainThreadRunHandler());
    }


    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        if (r instanceof TaskNameRunnable){
            t.setName(((TaskNameRunnable) r).getTaskName());
        }
        super.beforeExecute(t, r);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        if (r instanceof TaskNameRunnable){
            int status = ((TaskNameRunnable) r).getStatus();
            if (status < 0){
                System.out.println("===== " + ((TaskNameRunnable) r).getTaskName() + " is failure.");
            }
        }
        super.afterExecute(r, t);
    }

    /**
     * 获取任务队列中的任务数量
     * @return
     */
    public int getWorkQueueSize(){
        return getQueue().size();
    }

    static class MainThreadRunHandler implements RejectedExecutionHandler{
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
        return MainThreadRunHandler.rejectedCount.get();
    }
}
