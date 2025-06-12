package br.com.ecommerce.ecom.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@UtilityClass
@Slf4j
public class TraceIdGenerator {

    private static final ThreadLocal<String> TRACE_ID = new ThreadLocal<>();

    public void generateNewTraceId() {
        String traceId = UUID.randomUUID().toString();
        TRACE_ID.set(traceId);
        log.debug("Generated new traceId: {}", traceId);
    }

    public String getTraceId() {
        return TRACE_ID.get() != null ? TRACE_ID.get() : "UNKNOWN_TRACE_ID";
    }

    public void clear() {
        TRACE_ID.remove();
    }
}