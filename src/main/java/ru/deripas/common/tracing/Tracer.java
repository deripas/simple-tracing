package ru.deripas.common.tracing;

import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * Span supplier, can read {@code Span} from key-value source or get from current thread.
 */
public interface Tracer {

    /**
     * Read span from external source.
     *
     * @param reader source
     * @return new span
     */
    Span extract(Function<String, String> reader);

    /**
     * Get current thread span.
     *
     * @return current thread span or {@code null}
     */
    @Nullable
    Span current();
}
