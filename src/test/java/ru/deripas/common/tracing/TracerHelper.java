package ru.deripas.common.tracing;

import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;

import static ru.deripas.common.tracing.impl.extractor.ExampleHeaderExtractor.REQUEST_ID;

@UtilityClass
public class TracerHelper {

    public static Span generate() {
        return Tracing.extract(key -> null);
    }

    public static Span create(String id) {
        return Tracing.extract(key -> REQUEST_ID.equals(key) ? id : null);
    }

    public static String getId(Span span) {
        Map<String, String> map = new HashMap<>();
        span.inject(map::put);
        return map.get(REQUEST_ID);
    }
}
