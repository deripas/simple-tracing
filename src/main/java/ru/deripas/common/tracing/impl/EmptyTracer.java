package ru.deripas.common.tracing.impl;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.deripas.common.tracing.Span;
import ru.deripas.common.tracing.Tracer;

import javax.annotation.Nullable;
import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public final class EmptyTracer implements Tracer {

    public static final Tracer INSTANCE = new EmptyTracer();

    @Override
    public EmptySpan extract(Function<String, String> reader) {
        return EmptySpan.INSTANCE;
    }

    @Nullable
    @Override
    public Span current() {
        return EmptySpan.INSTANCE;
    }
}
