package com.starylwu.starasync.future;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.locks.LockSupport;

@SuppressWarnings("serial")
final class Signaller extends AbstractCompletion implements ForkJoinPool.ManagedBlocker {
    /**
     * wait time if timed
     */
    long nanos;
    /**
     * non-zero if timed
     */
    final long deadline;
    /**
     * > 0: interruptible, < 0: interrupted
     */
    volatile int interruptControl;
    volatile Thread thread;

    Signaller(boolean interruptible, long nanos, long deadline) {
        this.thread = Thread.currentThread();
        this.interruptControl = interruptible ? 1 : 0;
        this.nanos = nanos;
        this.deadline = deadline;
    }

    @Override
    final JobFuture<?> tryFire(int ignore) {
        // no need to atomically claim
        Thread w;
        if ((w = thread) != null) {
            thread = null;
            LockSupport.unpark(w);
        }
        return null;
    }

    @Override
    public boolean isReleasable() {
        if (thread == null){
            return true;
        }
        if (Thread.interrupted()) {
            int i = interruptControl;
            interruptControl = -1;
            if (i > 0){
                return true;
            }
        }
        if (deadline != 0L &&
            (nanos <= 0L || (nanos = deadline - System.nanoTime()) <= 0L)) {
            thread = null;
            return true;
        }
        return false;
    }

    @Override
    public boolean block() {
        if (isReleasable()){
            return true;
        }
        else if (deadline == 0L){
            LockSupport.park(this);
        }
        else if (nanos > 0L){
            LockSupport.parkNanos(this, nanos);
        }
        return isReleasable();
    }

    @Override
    final boolean isLive() {
        return thread != null;
    }
}