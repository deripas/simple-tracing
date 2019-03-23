package ru.deripas.common.tracing.instrument;

import lombok.SneakyThrows;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.deripas.common.tracing.Span;
import ru.deripas.common.tracing.Tracing;
import ru.deripas.common.tracing.impl.SimpleTracer;
import ru.deripas.common.tracing.impl.extractor.ExampleHeaderExtractor;
import ru.deripas.common.tracing.instrument.async.TraceExecutor;
import ru.deripas.common.tracing.instrument.async.TraceExecutorService;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static ru.deripas.common.tracing.TracerHelper.generate;
import static ru.deripas.common.tracing.TracerHelper.getId;

public class ExecutorServiceTest {

    private static ExecutorService executor;

    @BeforeClass
    public static void init() {
        Tracing.init(new SimpleTracer(new ExampleHeaderExtractor()));
        executor = Executors.newFixedThreadPool(2);
    }

    @AfterClass
    public static void clear() {
        Tracing.reset();
        executor.shutdownNow();
    }

    @SneakyThrows
    @Test
    public void testSimpleExecutor() {
        Span span = generate();
        assertNotNull(getId(span));

        span.invoke(() -> {
            assertEquals(getId(span), getId(Tracing.current()));

            String spanId = executor.submit(() -> {
                return getId(Tracing.current());
            }).get();

            // executor lost span!!!
            assertNull(spanId);
        });
    }

    @SneakyThrows
    @Test
    public void testWrappedExecutor() {
        Span span = generate();
        assertNotNull(getId(span));

        span.invoke(() -> {
            assertEquals(getId(span), getId(Tracing.current()));

            String spanId = new TraceExecutorService(executor).submit(() -> {
                return getId(Tracing.current());
            }).get();

            // executor save parent span !!!
            assertNotNull(spanId);
            assertEquals(getId(span), spanId);
        });
    }
}
