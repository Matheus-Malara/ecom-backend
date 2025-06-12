package br.com.ecommerce.ecom.config.filter;

import br.com.ecommerce.ecom.util.TraceIdGenerator;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(1)
@Slf4j
public class TraceIdFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        try {
            TraceIdGenerator.generateNewTraceId();
            String traceId = TraceIdGenerator.getTraceId();

            HttpServletRequest httpRequest = (HttpServletRequest) request;
            log.info("[{}] Incoming request: {} {}", traceId, httpRequest.getMethod(), httpRequest.getRequestURI());

            chain.doFilter(request, response);

        } finally {
            TraceIdGenerator.clear();
        }
    }
}
