package ru.deripas.common.tracing;

import io.reactivex.functions.Action;

import java.util.concurrent.Callable;
import java.util.function.BiConsumer;

/**
 * Tracing context holder.
 */
public interface Span {

    /**
     * Save span to consumer
     * @param writer
     */
    void inject(BiConsumer<String, String> writer);

    /**
     * Apply tracing context for action task
     * @param runnable
     */
    void invoke(Action runnable);

    /**
     * Apply tracing context for callable task
     * @param callable
     * @param <T>
     * @return
     */
    <T> T invoke(Callable<T> callable);
}
