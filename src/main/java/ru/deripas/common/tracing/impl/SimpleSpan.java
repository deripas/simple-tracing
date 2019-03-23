package ru.deripas.common.tracing.impl;

import io.reactivex.functions.Action;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.ToString;
import org.slf4j.MDC;
import ru.deripas.common.tracing.Span;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;

@RequiredArgsConstructor
@ToString
class SimpleSpan implements Span {

    private static final ThreadLocal<SimpleSpan> CURRENT = new ThreadLocal<>();
    private final Map<String, String> context;

    @Nullable
    static SimpleSpan current() {
        return CURRENT.get();
    }

    @Override
    public void inject(BiConsumer<String, String> writer) {
        context.forEach(writer);
    }

    @Override
    @SneakyThrows
    public void invoke(Action runnable) {
        SimpleSpan span = start();
        try {
            runnable.run();
        } finally {
            stop(span);
        }
    }

    @Override
    @SneakyThrows
    public <T> T invoke(Callable<T> callable) {
        SimpleSpan span = start();
        try {
            return callable.call();
        } finally {
            stop(span);
        }
    }

    private SimpleSpan start() {
        return change(CURRENT.get(), this);
    }

    private void stop(SimpleSpan span) {
        if (span == null) {
            close();
        } else {
            change(this, span);
        }
    }

    private SimpleSpan change(SimpleSpan from, SimpleSpan to) {
        if (from != to) {
            CURRENT.set(to);
            to.context.forEach(MDC::put);
        }
        return from;
    }

    private void close() {
        CURRENT.remove();
        context.keySet().forEach(MDC::remove);
    }
}

