package com.starylwu.starasync.pool;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author wuyulong
 * @date 2019/1/30
 * @desc 线程池的监控
 */
public class ThreadPoolMonitor {

    private ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);

    private Long taskCount = 0L;

    private Long rejectedCount = 0L;

    private Long completedTaskCount = 0L;

    public void monitor(ParentThreadPool pool){
        executor.scheduleAtFixedRate(()->{
            long taskCount = pool.getTaskCount();
            long rejectedCount = pool.getRejectedCount();
            int notWorkCount = pool.getWorkQueueSize();
            int activeThreadCount = pool.getActiveCount();
            long completedTaskCount = pool.getCompletedTaskCount();
            //监控线程池数据并报警(发送邮件和钉钉)
            //计算每10秒的增速
            System.out.println("taskCount=" + (taskCount - this.taskCount)/10);
            this.taskCount = taskCount;
            System.out.println("rejectedCount=" + (rejectedCount - this.rejectedCount)/10);
            this.rejectedCount = rejectedCount;
            System.out.println("notWorkCount=" + notWorkCount);
            System.out.println("activeThreadCount=" + activeThreadCount);
            System.out.println("completedTaskCount=" + (completedTaskCount - this.completedTaskCount)/10);
            this.completedTaskCount = completedTaskCount;
            System.out.println("========================");

        }, 2, 10, TimeUnit.SECONDS);
    }
}
