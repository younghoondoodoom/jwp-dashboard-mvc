package webmvc.org.springframework.web.servlet.resolver;

import java.util.List;

public class ViewResolvers {

    private final List<ViewResolver> values;

    public ViewResolvers(final List<ViewResolver> values) {
        this.values = values;
    }

    public ViewResolver findSupportedViewResolver(final String viewName) {
        return values.stream()
                .filter(it -> it.supports(viewName))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("View Not Supported"));
    }
}
