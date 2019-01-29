package com.starylwu.starasync.pool;

import com.starylwu.starasync.pool.JobFuture;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @Auther: Wuyulong
 * @Date: 2019/1/26 14:33
 * @Description:
 */
public class JobFutureTest {
    private static Executor executor = Executors.newWorkStealingPool(5);

    @Test
    public void supplyAsync() throws ExecutionException, InterruptedException {
        Long start = System.currentTimeMillis();
        JobFuture<String> job1 = JobFuture.supplyAsync(() -> {
            try {
                System.out.println("Thread Name=[ " + Thread.currentThread().getName() + " ]");
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "job1";
        }, executor);

        JobFuture<String> job2 = JobFuture.supplyAsync(() -> {
            try {
                System.out.println("Thread Name=[ " + Thread.currentThread().getName() + " ]");
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "job2";
        }, executor);

        JobFuture<String> job3 = JobFuture.supplyAsync(() -> {
            try {
                System.out.println("Thread Name=[ " + Thread.currentThread().getName() + " ]");
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "job3";
        }, executor);

        JobFuture<String> job4 = JobFuture.supplyAsync(() -> {
            try {
                System.out.println("Thread Name=[ " + Thread.currentThread().getName() + " ]");
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "job4";
        }, executor);

        System.out.println(job1.get() + job2.get() + job3.get() + job4.get());
        System.out.println("use time = " + (System.currentTimeMillis() - start));
    }

    @Test
    public void thenApplyAsync() throws ExecutionException, InterruptedException {
        Long start = System.currentTimeMillis();
        JobFuture<String> job1 = JobFuture.supplyAsync(() -> {
            try {
                System.out.println("Thread Name=[ " + Thread.currentThread().getName() + " ]");
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "job1";
        }, executor).thenApplyAsync(str -> str + "then ");

        JobFuture<String> job2 = JobFuture.supplyAsync(() -> {
            try {
                System.out.println("Thread Name=[ " + Thread.currentThread().getName() + " ]");
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "job2";
        }, executor).thenApplyAsync(str -> str + "then ");

        JobFuture<String> job3 = JobFuture.supplyAsync(() -> {
            try {
                System.out.println("Thread Name=[ " + Thread.currentThread().getName() + " ]");
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "job3";
        }, executor).thenApplyAsync(str -> str + "then ");

        JobFuture<Integer> job4 = JobFuture.supplyAsync(() -> {
            try {
                System.out.println("Thread Name=[ " + Thread.currentThread().getName() + " ]");
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "job4";
        }, executor).thenApplyAsync(str -> str.length());

        System.out.println(job1.get() + job2.get() + job3.get() + job4.get());
        System.out.println("use time = " + (System.currentTimeMillis() - start));
    }
}
