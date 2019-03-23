package ru.deripas.common.tracing.instrument.async;

import lombok.RequiredArgsConstructor;
import ru.deripas.common.tracing.Span;
import ru.deripas.common.tracing.Tracing;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class TraceCallable<T> implements Callable<T> {

    private final Callable<T> callable;
    private final Span span;

    @Override
    public T call() throws Exception {
        return span.invoke(callable);
    }

    public static <T> TraceCallable<T> wrap(Callable<T> callable) {
        return callable instanceof TraceCallable
                ? (TraceCallable<T>) callable
                : new TraceCallable<>(callable, Tracing.current());
    }
}