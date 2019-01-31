package com.starylwu.starasync.pool;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wuyulong
 * @date 2019/1/30
 * @desc 线程池及监控测试
 */
public class PoolTest {

    private AtomicInteger integer = new AtomicInteger(0);

    private ParentThreadPool pool = new ParentThreadPool(3, 5, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(50), new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("parent-thread-" + integer.getAndIncrement());
            return thread;
        }
    });
    Map<Integer, Integer> map = new HashMap<>(100000);
    @Test
    public void test(){
        ThreadPoolMonitor threadPoolMonitor = new ThreadPoolMonitor();
        threadPoolMonitor.monitor(pool);
        AtomicInteger count = new AtomicInteger(0);
        while (count.get()<10000){
            int c = count.getAndIncrement();
            pool.execute(new TaskNameRunnable("testJob" + c) {
                @Override
                public void job() throws Exception {
                    if (c%9 == 0){
                        throw new RuntimeException("ex");
                    }
                    Thread.sleep(50);
                }
            });
            try {
                Thread.sleep(7);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (Map.Entry<Integer, Integer> m : map.entrySet()){
            Integer src = m.getKey();
            Integer dep = map.get(src + 1);
            if (dep == null || (dep - src) != 1){
                System.out.println(src + " - " + dep);
            }
        }
    }
}
