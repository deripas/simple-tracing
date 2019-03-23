package ru.deripas.common.tracing;

import lombok.SneakyThrows;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.MDC;
import ru.deripas.common.tracing.impl.SimpleTracer;
import ru.deripas.common.tracing.impl.extractor.ExampleHeaderExtractor;

import java.util.UUID;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static ru.deripas.common.tracing.TracerHelper.create;
import static ru.deripas.common.tracing.TracerHelper.generate;
import static ru.deripas.common.tracing.TracerHelper.getId;
import static ru.deripas.common.tracing.impl.extractor.ExampleHeaderExtractor.REQUEST_ID;

public class TracingTest {

    @BeforeClass
    public static void init() {
        Tracing.init(new SimpleTracer(new ExampleHeaderExtractor()));
    }

    @AfterClass
    public static void clear() {
        Tracing.reset();
    }


    @Test
    public void testCreateAutoGenerate() {
        Span span = generate();
        assertNotNull(getId(span));
    }

    @Test
    public void testCreateFixedId() {
        String id = UUID.randomUUID().toString();
        Span span = create(id);
        assertEquals(id, getId(span));
    }

    @Test
    public void testNotTraceContext() {
        assertNull(getId(Tracing.current()));
    }

    @Test
    public void testSimpleTraceContext() {
        assertNull(getId(Tracing.current()));

        String id = UUID.randomUUID().toString();
        Span span = create(id);
        // span not yet active here
        assertNull(getId(Tracing.current()));

        Runnable action = mock(Runnable.class);
        span.invoke(() -> {
            action.run();
            assertEquals(id, getId(Tracing.current()));
        });

        verify(action).run();
        assertNull(getId(Tracing.current()));
    }

    @Test
    public void testDualTraceContext() {
        assertNull(getId(Tracing.current()));

        Span span1 = generate();
        Span span2 = generate();
        assertNotEquals(getId(span1), getId(span2));

        Runnable action1 = mock(Runnable.class);
        Runnable action2 = mock(Runnable.class);
        Runnable action3 = mock(Runnable.class);

        span1.invoke(() -> {
            // call in span1 context
            action1.run();
            assertEquals(getId(span1), getId(Tracing.current()));
            assertEquals(getId(span1), MDC.get(REQUEST_ID));

            // recurrent call in span1 context
            span1.invoke(() -> {
                action2.run();
                assertEquals(getId(span1), getId(Tracing.current()));
                assertEquals(getId(span1), MDC.get(REQUEST_ID));
            });
            // already span1 context
            assertEquals(getId(span1), getId(Tracing.current()));
            assertEquals(getId(span1), MDC.get(REQUEST_ID));

            // call in other span2 context
            span2.invoke(() -> {
                // in span2 context
                action3.run();
                assertEquals(getId(span2), getId(Tracing.current()));
                assertEquals(getId(span2), MDC.get(REQUEST_ID));
            });

            // in span1 context
            assertEquals(getId(span1), getId(Tracing.current()));
            assertEquals(getId(span1), MDC.get(REQUEST_ID));
        });

        verify(action1).run();
        verify(action2).run();
        verify(action3).run();
        assertNull(getId(Tracing.current()));
        assertNull(MDC.get(REQUEST_ID));
    }

    @SneakyThrows
    @Test
    public void testTraceContextInOtherThread() {
        Span span = generate();
        assertNull(getId(Tracing.current()));

        Runnable action = mock(Runnable.class);
        Executors.newSingleThreadExecutor().submit(() -> {
            assertNull(getId(Tracing.current()));
            assertNull(MDC.get(REQUEST_ID));

            span.invoke(() -> {
                action.run();
                assertEquals(getId(span), getId(Tracing.current()));
                assertEquals(getId(span), MDC.get(REQUEST_ID));
            });

            assertNull(getId(Tracing.current()));
            assertNull(MDC.get(REQUEST_ID));
        }).get();

        verify(action).run();
        assertNull(getId(Tracing.current()));
    }
}
