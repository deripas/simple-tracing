package ru.deripas.common.tracing.impl.extractor;

import lombok.RequiredArgsConstructor;
import ru.deripas.common.tracing.impl.SimpleTracer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class ExampleHeaderExtractor implements SimpleTracer.SpanContextExtractor {

    public static final String REQUEST_ID = "x-request-id";
    private final Supplier<String> idGenerator;

    public ExampleHeaderExtractor() {
        this(() -> UUID.randomUUID().toString());
    }

    @Override
    public Map<String, String> apply(Function<String, String> reader) {
        Map<String, String> context = new HashMap<>();
        context.put(REQUEST_ID, reader.apply(REQUEST_ID));
        context.computeIfAbsent(REQUEST_ID, key -> idGenerator.get());
        return context;
    }
}
