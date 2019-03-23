package ru.deripas.common.tracing.impl;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import ru.deripas.common.tracing.Span;
import ru.deripas.common.tracing.Tracer;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Function;

@RequiredArgsConstructor
@ToString
public class SimpleTracer implements Tracer {

    private final SpanContextExtractor extractor;

    @Override
    public Span extract(Function<String, String> reader) {
        return new SimpleSpan(extractor.apply(reader));
    }

    @Nullable
    @Override
    public Span current() {
        return SimpleSpan.current();
    }

    public interface SpanContextExtractor extends Function<Function<String, String>, Map<String, String>> {

    }
}
