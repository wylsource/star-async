package com.starylwu.starasync.future;

import java.util.concurrent.ForkJoinTask;

@SuppressWarnings("serial")
abstract class AbstractCompletion extends ForkJoinTask<Void>
    implements Runnable, AsynchronousCompletionJob {

    volatile AbstractCompletion next;

    /**
     * Performs completion action if triggered, returning a
     * dependent that may need propagation, if one exists.
     *
     * @param mode SYNC, ASYNC, or NESTED
     */
    abstract JobFuture<?> tryFire(int mode);

    /** Returns true if possibly still triggerable. Used by cleanStack. */
    abstract boolean isLive();

    @Override
    public final void run() {
        tryFire(1);
    }

    @Override
    public final boolean exec() {
        tryFire(1);
        return true;
    }

    @Override
    public final Void getRawResult() {
        return null;
    }

    @Override
    public final void setRawResult(Void v) {}
}