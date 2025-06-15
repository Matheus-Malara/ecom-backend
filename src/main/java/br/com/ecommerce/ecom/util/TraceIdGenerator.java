package br.com.ecommerce.ecom.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@UtilityClass
@Slf4j
public class TraceIdGenerator {

    private static final InheritableThreadLocal<String> TRACE_ID = new InheritableThreadLocal<>();

    public void generateNewTraceId() {
        String traceId = UUID.randomUUID().toString();
        TRACE_ID.set(traceId);
        log.debug("Generated new traceId: {}", traceId);
    }

    public String getTraceId() {
        String traceId = TRACE_ID.get();
        return traceId != null ? traceId : "UNKNOWN_TRACE_ID";
    }

    public void clear() {
        TRACE_ID.remove();
        log.debug("Cleared traceId from thread");
    }
}