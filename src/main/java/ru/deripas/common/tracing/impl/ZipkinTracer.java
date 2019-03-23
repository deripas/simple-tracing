package ru.deripas.common.tracing.impl;

import brave.propagation.B3Propagation;
import brave.propagation.Propagation;
import brave.propagation.TraceContext;
import lombok.AllArgsConstructor;
import ru.deripas.common.tracing.Span;
import ru.deripas.common.tracing.Tracer;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.Function;

@AllArgsConstructor
public class ZipkinTracer implements Tracer {

    public static final Propagation<String> B3_STRING = B3Propagation.FACTORY.create(Propagation.KeyFactory.STRING);
    public static final TraceContext.Extractor<Function<String, String>> MAP_EXTRACTOR = B3_STRING.extractor(Function::apply);
    public static final TraceContext.Injector<BiConsumer<String, String>> MAP_INJECTOR = B3_STRING.injector(BiConsumer::accept);

    private final brave.Tracer tracer;

    @Override
    public Span extract(Function<String, String> reader) {
        return create(tracer.nextSpan(MAP_EXTRACTOR.extract(reader)));
    }

    @Nullable
    @Override
    public Span current() {
        return create(tracer.currentSpan());
    }

    private Span create(brave.Span span) {
        return new ZipkinSpan(span != null ? span.context() : null, tracer);
    }
}
