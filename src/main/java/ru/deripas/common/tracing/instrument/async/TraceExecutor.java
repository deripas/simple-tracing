package ru.deripas.common.tracing.instrument.async;

import lombok.RequiredArgsConstructor;

import java.util.concurrent.Executor;

import static java.util.Objects.requireNonNull;
import static ru.deripas.common.tracing.instrument.async.TraceRunnable.wrap;

@RequiredArgsConstructor
public class TraceExecutor implements Executor {

    private final Executor delegate;

    @Override
    public void execute(Runnable command) {
        delegate.execute(wrap(requireNonNull(command)));
    }
}
