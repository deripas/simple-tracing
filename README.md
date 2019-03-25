# Simple Tracing Library

Simple tracing with utility class `Tracing`, for example:
```java
//current context
Span span = Tracing.current()
...
//in other thread you can do
     span.invoke(() -> {
        // this code in 'span' context
     });
```

## Simple Use Case (Example)
Simple case: you need to stretch certain http request headers.

* Init `SimpleTracer` with some header extractor implementation, for example `ExampleExtractor`. Where `ExampleExtractor` reading custom tracing header from `Strnig` key-value source.
```java
Tracing.init(new SimpleTracer(new ExampleExtractor()));
```

* Create `TraceFilter` for reading tracig from request header:
```java
public class TraceFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws ServletException {
        if (!(servletRequest instanceof HttpServletRequest) || !(servletResponse instanceof HttpServletResponse)) {
            throw new ServletException("Filter just supports HTTP requests");
        }
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        Tracing.extract(request::getHeader)
                .invoke(() -> chain.doFilter(servletRequest, servletResponse));
    }
}
```

* Create `TraceClientHttpRequestInterceptor` for writig tracig to `RestTemplate` client request header:
```java
public class TraceClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        Tracing.current().inject(request.getHeaders()::set);
        return execution.execute(request, body);
    }
}
```

* Proffit! Headers from http request contains in outgoing client request.
```java
@RestController
public class PublicController {

    private final RestTemplate restTemplateForService;

    @GetMapping
    public MyResponse someMethod(...) {
        ...
        return restTemplateForService.getForObject("/uri", request, MyResponse.class);
    }
}
```

## Zipkin Use Case (using [spring-cloud-sleuth](https://spring.io/projects/spring-cloud-sleuth))
* Init `ZipkinTracer`.
```java
    @Autowired
    private brave.Tracer tracer;

    public void init() {
        Tracing.init(new ZipkinTracer(tracer));
    }
```

* Proffit! You can trace between Spring and other non String context.
```java
//current context, Zipkin loggig enable
Span span = Tracing.current()
Map<String, String> meta = ...
Tracing.current().inject(meta::put);
// for exapmle, write to TCP
...

//in other thread, for example, read from TCP
Map<String, String> meta = ...
Tracing.extract(meta::get).invoke(() -> {
        // this code in 'span' context, integrated with Zipkin and logging
     });
```
