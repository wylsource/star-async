package com.starylwu.starasync.pool;

/**
 * @author wuyulong
 * @date 2019/1/31
 * @desc 带任务名字的任务
 */
public abstract class TaskNameRunnable implements Runnable{

    private String taskName;

    private volatile int status = 0;

    public TaskNameRunnable(String taskName) {
        this.taskName = taskName + "#" + System.nanoTime();
    }

    public String getTaskName() {
        return taskName;
    }

    public int getStatus() {
        return status;
    }

    public abstract void job() throws Exception;

    @Override
    public void run() {
        try {
            job();
            status = 1;
        } catch (Exception e) {
            status = -1;
        }
    }
}
