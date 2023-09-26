package webmvc.org.springframework.web.servlet.handler.adaptor;

import webmvc.org.springframework.web.servlet.mvc.HandlerAdaptor;
import java.util.List;

public class HandlerAdaptors {

    private final List<HandlerAdaptor> values;

    public HandlerAdaptors(final List<HandlerAdaptor> values) {
        this.values = values;
    }

    public HandlerAdaptor findHandlerAdaptor(final Object handler) {
        return values.stream()
                .filter(value -> value.supports(handler))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Handler Adaptor Not Found"));
    }
}
