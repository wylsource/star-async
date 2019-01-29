package com.starylwu.starasync.future;

/**
 * @author wuyulong
 * @date 2019/1/29
 * @desc 异常对象
 */
final class ExResult {

    final Throwable throwable;

    public ExResult(Throwable throwable) {
        this.throwable = throwable;
    }
}
