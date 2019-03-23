package ru.deripas.common.tracing;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import ru.deripas.common.tracing.impl.ZipkinTracer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.cloud.sleuth.instrument.messaging.TraceMessageHeaders.SPAN_ID_NAME;
import static org.springframework.cloud.sleuth.instrument.messaging.TraceMessageHeaders.TRACE_ID_NAME;
import static ru.deripas.common.tracing.TracerHelper.getId;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {ZipkinTracingTest.Config.class})
public class ZipkinTracingTest {

    static final String TRACE_ID_NAME = "X-B3-TraceId";
    static final String SPAN_ID_NAME = "X-B3-SpanId";

    @Configuration
    @EnableAutoConfiguration
    static class Config {

        @Bean
        public ExecutorService executor() {
            return Executors.newFixedThreadPool(2);
        }
    }

    @Autowired
    private brave.Tracer tracer;

    @Autowired
    private ExecutorService executor;

    @Before
    public void init() {
        Tracing.init(new ZipkinTracer(tracer));
    }

    @After
    public void clear() {
        Tracing.reset();
    }

    @Test
    public void test() {
        Span span = Tracing.extract(s -> null);
        span.invoke(() -> {
            Map<String, String> in = new HashMap<>();
            Tracing.current().inject(in::put);

            Map<String, String> out = executor.submit(() -> {
                Map<String, String> map = new HashMap<>();
                Tracing.current().inject(map::put);
                return map;
            }).get();

            // executor save parent span !!!
            assertNotEquals(in, out);
            assertEquals(in.get(TRACE_ID_NAME), out.get(TRACE_ID_NAME));
        });
    }
}
