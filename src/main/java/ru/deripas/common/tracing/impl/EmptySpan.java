package ru.deripas.common.tracing.impl;

import io.reactivex.functions.Action;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.ToString;
import ru.deripas.common.tracing.Span;

import java.util.concurrent.Callable;
import java.util.function.BiConsumer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public final class EmptySpan implements Span {

    public static final EmptySpan INSTANCE = new EmptySpan();

    @Override
    public void inject(BiConsumer<String, String> writer) {
        // no action
    }

    @SneakyThrows
    @Override
    public void invoke(Action runnable) {
        runnable.run();
    }

    @SneakyThrows
    @Override
    public <T> T invoke(Callable<T> callable) {
        return callable.call();
    }
}
