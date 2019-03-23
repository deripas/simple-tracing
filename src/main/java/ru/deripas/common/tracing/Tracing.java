package ru.deripas.common.tracing;


import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.deripas.common.tracing.impl.EmptySpan;
import ru.deripas.common.tracing.impl.EmptyTracer;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

/**
 * Utility class, facade layer for {@code Tracer} implementation.
 */
@Slf4j
@UtilityClass
public class Tracing {

    private static final AtomicReference<Tracer> CURRENT = new AtomicReference<>(EmptyTracer.INSTANCE);

    /**
     * Update current used implementation.
     *
     * @param tracer new implementation
     * @return
     */
    public static Tracer init(Tracer tracer) {
        if (!CURRENT.compareAndSet(EmptyTracer.INSTANCE, tracer)) {
            throw new IllegalStateException("Tracer already init");
        }
        return tracer;
    }

    public static void reset() {
        CURRENT.set(EmptyTracer.INSTANCE);
    }

    /**
     * Read span from external source.
     *
     * @param reader source
     * @return new span
     */
    public static Span extract(Function<String, String> reader) {
        return CURRENT.get().extract(reader);
    }

    /**
     * Get current thread span.
     *
     * @return current thread span or empty
     */
    public static Span current() {
        return defaultIfNull(CURRENT.get().current(), EmptySpan.INSTANCE);
    }

}
