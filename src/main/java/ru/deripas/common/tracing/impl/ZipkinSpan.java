package ru.deripas.common.tracing.impl;

import brave.ScopedSpan;
import brave.Tracer;
import brave.internal.Nullable;
import brave.propagation.TraceContext;
import io.reactivex.functions.Action;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import ru.deripas.common.tracing.Span;

import java.util.concurrent.Callable;
import java.util.function.BiConsumer;

@AllArgsConstructor
public class ZipkinSpan implements Span {

    @Nullable
    private final TraceContext context;
    private final Tracer tracer;

    @Override
    public void inject(BiConsumer<String, String> writer) {
        if(context != null) {
            ZipkinTracer.MAP_INJECTOR.inject(context, writer);
        }
    }

    @SneakyThrows
    @Override
    public void invoke(Action runnable) {
        ScopedSpan s = tracer.startScopedSpanWithParent("async", context);
        try {
            runnable.run();
        } catch (Exception | Error e) {
            s.error(e);
            throw e;
        } finally {
            s.finish();
        }
    }

    @SneakyThrows
    @Override
    public <T> T invoke(Callable<T> callable) {
        ScopedSpan s = tracer.startScopedSpanWithParent("async", context);
        try {
            return callable.call();
        } catch (Exception | Error e) {
            s.error(e);
            throw e;
        } finally {
            s.finish();
        }
    }
}
