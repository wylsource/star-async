package com.starylwu.starasync.future;

/**
 * @author wuyulong
 * @date 2019/1/29
 * @desc job执行异常
 */
public class CompletionException extends RuntimeException {
    private static final long serialVersionUID = 7830266012832686185L;

    protected CompletionException() { }

    protected CompletionException(String message) {
        super(message);
    }

    public CompletionException(String message, Throwable cause) {
        super(message, cause);
    }

    public CompletionException(Throwable cause) {
        super(cause);
    }
}
