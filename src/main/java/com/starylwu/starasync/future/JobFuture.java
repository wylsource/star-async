package com.starylwu.starasync.future;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author wuyulong
 * @date 2019/1/29
 * @desc 任务工作实现
 * 函数介绍：
 * 1.Supplier 是一个工厂，里面只有一个get方法，用于获取结果，作为一个方法链的第一个方法参数较适合，因为该函数不需要入参，第一个方法一般情况下也没有参数的。
 * 2.Function 是一个函数，里面有个apply方法，用户接受参数，并且返回结果，用于方法链中的中间方法参数较合适。接受上个方法的返回值并返回当前方法的执行结果
 */
public class JobFuture<T> implements Future<T>, JobStage<T> {

    /**
     * It's a thread pool for run job.
     */
    static Executor executor;

    /**
     * It's job result,and more than one thread can either it.
     */
    volatile Object result ;

    volatile AbstractCompletion stack;

    private static final sun.misc.Unsafe UNSAFE;
    private static final long RESULT;
    private static final long STACK;
    private static final long NEXT;
    static {
        try {
            final sun.misc.Unsafe u;
            UNSAFE = u = ThreadLocalRandom.getUnsafe();
            Class<?> k = CompletableFuture.class;
            RESULT = u.objectFieldOffset(k.getDeclaredField("result"));
            STACK = u.objectFieldOffset(k.getDeclaredField("stack"));
            NEXT = u.objectFieldOffset
                    (AbstractCompletion.class.getDeclaredField("next"));
        } catch (Exception x) {
            throw new Error(x);
        }
    }
    
    /**
     * It's exception result.
     */
    private static final ExResult DEFAULT_EXRESULT = new ExResult(null);

    public static void setExecutor(Executor e) {
        executor = e;
    }

    public static  <U> JobFuture<U> supply(Supplier<U> supplier){
        return asnycSupplyStage(supplier, executor);
    }

    private static  <U> JobFuture<U> asnycSupplyStage(Supplier<U> supplier, Executor executor){
        if (supplier == null){
            throw new NullPointerException();
        }
        //there must new object...
        JobFuture<U> depFuture = new JobFuture<>();
        //use thread pool exec
        executor.execute(new Supply<>(depFuture, supplier));
        return depFuture;
    }

    public <U> JobFuture<U> then(Function<? super T, ? extends U> function){

        return null;
    }

    /**
     * 使用 cas 设置值，正常情况只能设置成功一次
     * @param result
     * @return
     */
    protected final boolean setResult(T result){
        return UNSAFE.compareAndSwapObject(this, RESULT, null, (result == null) ? DEFAULT_EXRESULT : result);
    }

    /**
     * 使用 cas 设置异常，正常情况只能设置成功一次
     * @param throwable
     * @return
     */
    protected final boolean setExResult(Throwable throwable){
        return UNSAFE.compareAndSwapObject(this, RESULT, null, encodeThrowable(throwable));
    }

    /**
     * 校验封装异常信息
     * @param throwable
     * @return
     */
    private ExResult encodeThrowable(Throwable throwable) {
        return new ExResult((throwable instanceof CompletionException) ? throwable : new CompletionException(throwable));
    }






    //---------------------------

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        Object r;
        return reportGet((r = result) == null ? waitingGet(true) : r);
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return null;
    }

    /**
     * 转化result
     */
    private static <T> T reportGet(Object r)
            throws InterruptedException, ExecutionException {
        if (r == null){
            // by convention below, null means interrupted
            throw new InterruptedException();
        }
        if (r instanceof ExResult) {
            Throwable x, cause;
            if ((x = ((ExResult)r).throwable) == null){
                return null;
            }
            if (x instanceof CancellationException){
                throw (CancellationException)x;
            }
            if ((x instanceof CompletionException) &&
                    (cause = x.getCause()) != null){
                x = cause;
            }
            throw new ExecutionException(x);
        }
        @SuppressWarnings("unchecked")
        T t = (T) r;
        return t;
    }

    /**
     * Returns raw result after waiting, or null if interruptible and
     * interrupted.
     */
    private Object waitingGet(boolean interruptible) {
        Signaller q = null;
        boolean queued = false;
        int spins = -1;
        Object r;
        while ((r = result) == null) {
            if (spins < 0){
                // Use brief spin-wait on multiprocessors
                spins = (Runtime.getRuntime().availableProcessors() > 1) ?
                        1 << 8 : 0;
            }
            else if (spins > 0) {
                if (ThreadLocalRandom.nextSecondarySeed() >= 0){
                    --spins;
                }
            }
            else if (q == null){
                q = new Signaller(interruptible, 0L, 0L);
            }
            else if (!queued){
                queued = tryPushStack(q);
            }
            else if (interruptible && q.interruptControl < 0) {
                q.thread = null;
                cleanStack();
                return null;
            }
            else if (q.thread != null && result == null) {
                try {
                    ForkJoinPool.managedBlock(q);
                } catch (InterruptedException ie) {
                    q.interruptControl = -1;
                }
            }
        }
        if (q != null) {
            q.thread = null;
            if (q.interruptControl < 0) {
                if (interruptible){
                    // report interruption
                    r = null;
                }
                else{
                    Thread.currentThread().interrupt();
                }
            }
        }
        postComplete();
        return r;
    }

    final void postComplete() {
        JobFuture<?> f = this; AbstractCompletion h;
        while ((h = f.stack) != null ||
                (f != this && (h = (f = this).stack) != null)) {
            JobFuture<?> d; AbstractCompletion t;
            if (f.casStack(h, t = h.next)) {
                if (t != null) {
                    if (f != this) {
                        pushStack(h);
                        continue;
                    }
                    // detach
                    h.next = null;
                }
                f = (d = h.tryFire(1)) == null ? this : d;
            }
        }
    }

    final void cleanStack() {
        for (AbstractCompletion p = null, q = stack; q != null;) {
            AbstractCompletion s = q.next;
            if (q.isLive()) {
                p = q;
                q = s;
            }
            else if (p == null) {
                casStack(q, s);
                q = stack;
            }
            else {
                p.next = s;
                if (p.isLive()){
                    q = s;
                }
                else {
                    // restart
                    p = null;
                    q = stack;
                }
            }
        }
    }

    final boolean tryPushStack(AbstractCompletion c) {
        AbstractCompletion h = stack;
        lazySetNext(c, h);
        return UNSAFE.compareAndSwapObject(this, STACK, h, c);
    }

    final void pushStack(AbstractCompletion c) {
        do {} while (!tryPushStack(c));
    }

    static void lazySetNext(AbstractCompletion c, AbstractCompletion next) {
        UNSAFE.putOrderedObject(c, NEXT, next);
    }

    final boolean casStack(AbstractCompletion cmp, AbstractCompletion val) {
        return UNSAFE.compareAndSwapObject(this, STACK, cmp, val);
    }


    //-------------------内部类-------------


}
