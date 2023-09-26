package webmvc.org.springframework.web.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webmvc.org.springframework.web.servlet.handler.adaptor.HandlerAdaptors;
import webmvc.org.springframework.web.servlet.handler.mapping.HandlerMappings;
import webmvc.org.springframework.web.servlet.mvc.tobe.AnnotationHandlerAdaptor;
import webmvc.org.springframework.web.servlet.mvc.tobe.AnnotationHandlerMapping;
import webmvc.org.springframework.web.servlet.resolver.JsonViewResolver;
import webmvc.org.springframework.web.servlet.resolver.JspViewResolver;
import webmvc.org.springframework.web.servlet.resolver.ViewResolver;
import webmvc.org.springframework.web.servlet.resolver.ViewResolvers;
import java.util.List;

public class DispatcherServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(DispatcherServlet.class);

    private HandlerMappings handlerMapping;
    private HandlerAdaptors handlerAdaptors;
    private ViewResolvers viewResolvers;


    public DispatcherServlet() {
    }

    @Override
    public void init() {
        handlerMapping = new HandlerMappings(List.of(
                new AnnotationHandlerMapping()));
        handlerAdaptors = new HandlerAdaptors(List.of(
                new AnnotationHandlerAdaptor()));
        viewResolvers = new ViewResolvers(List.of(
                new JspViewResolver(),
                new JsonViewResolver()
        ));
        handlerMapping.initialize();
    }

    @Override
    protected void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
        log.debug("Method : {}, Request URI : {}", request.getMethod(), request.getRequestURI());
        try {
            final var optionalHandler = handlerMapping.getHandler(request);
            if (optionalHandler.isEmpty()) {
                log.warn("Handler Not Found");
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            final var handler = optionalHandler.get();

            final var handlerAdaptor = handlerAdaptors.findHandlerAdaptor(handler);
            final var modelAndView = handlerAdaptor.execute(request, response, handler);

            move(modelAndView, request, response);
        } catch (final Throwable e) {
            log.error("Exception : {}", e.getMessage(), e);
            throw new ServletException(e.getMessage());
        }
    }

    private void move(final ModelAndView modelAndView, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        final var viewName = modelAndView.getViewName();
        final ViewResolver viewResolver = viewResolvers.findSupportedViewResolver(viewName);
        final View view = viewResolver.resolveViewName(viewName);
        view.render(modelAndView.getModel(), request, response);
    }
}
