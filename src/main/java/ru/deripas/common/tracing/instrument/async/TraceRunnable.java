package ru.deripas.common.tracing.instrument.async;

import lombok.RequiredArgsConstructor;
import ru.deripas.common.tracing.Span;
import ru.deripas.common.tracing.Tracing;

@RequiredArgsConstructor
public class TraceRunnable implements Runnable {

    private final Runnable runnable;
    private final Span span;

    @Override
    public void run() {
        span.invoke(runnable::run);
    }

    public static TraceRunnable wrap(Runnable command) {
        return command instanceof TraceRunnable
                ? (TraceRunnable) command
                : new TraceRunnable(command, Tracing.current());
    }
}