package top.kwseeker.springboot.labs.netty.rocketmq.common.future;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * 在原 FutureTask 上增加一个 Runnable，用于记录原始任务，方便后续使用。
 */
public class FutureTaskExt<V> extends FutureTask<V> {
    private final Runnable runnable;

    public FutureTaskExt(final Callable<V> callable) {
        super(callable);
        this.runnable = null;
    }

    public FutureTaskExt(final Runnable runnable, final V result) {
        super(runnable, result);
        this.runnable = runnable;
    }

    public Runnable getRunnable() {
        return runnable;
    }
}