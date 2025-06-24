package br.com.ecommerce.ecom.exception;

import br.com.ecommerce.ecom.dto.responses.ApiError;
import br.com.ecommerce.ecom.dto.responses.StandardResponse;
import br.com.ecommerce.ecom.enums.ErrorCode;
import br.com.ecommerce.ecom.util.TraceIdGenerator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardResponse<List<ApiError>>> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String traceId = TraceIdGenerator.getTraceId();
        log.warn("[{}] Validation error on path {}: {} field errors", traceId, request.getRequestURI(), ex.getBindingResult().getFieldErrors().size());

        List<ApiError> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> ApiError.builder()
                        .field(error.getField())
                        .message(error.getDefaultMessage())
                        .build())
                .toList();

        StandardResponse<List<ApiError>> response = StandardResponse.<List<ApiError>>builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Validation failed")
                .errorCode(ErrorCode.VALIDATION_FAILED.name())
                .path(request.getRequestURI())
                .traceId(traceId)
                .data(errors)
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<StandardResponse<Void>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        String traceId = TraceIdGenerator.getTraceId();
        log.warn("[{}] Method not supported: {} on {}", traceId, ex.getMethod(), request.getRequestURI());
        return buildResponse(HttpStatus.METHOD_NOT_ALLOWED, "HTTP method not allowed", ErrorCode.INVALID_REQUEST, request, traceId);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<StandardResponse<Void>> handleMalformedJson(HttpMessageNotReadableException ex, HttpServletRequest request) {
        String traceId = TraceIdGenerator.getTraceId();
        log.warn("[{}] Malformed JSON request: {}", traceId, ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "Malformed request body", ErrorCode.INVALID_REQUEST, request, traceId);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<StandardResponse<Void>> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        String traceId = TraceIdGenerator.getTraceId();
        log.warn("[{}] Access denied on path {}: {}", traceId, request.getRequestURI(), ex.getMessage());
        return buildResponse(HttpStatus.FORBIDDEN, "Access denied", ErrorCode.ACCESS_DENIED, request, traceId);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardResponse<Void>> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        String traceId = TraceIdGenerator.getTraceId();
        log.warn("[{}] Resource not found: {}", traceId, ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), ErrorCode.RESOURCE_NOT_FOUND, request, traceId);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<StandardResponse<Void>> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        String traceId = TraceIdGenerator.getTraceId();
        log.warn("[{}] BusinessException: status={} code={} message='{}'", traceId, ex.getStatus(), ex.getErrorCode(), ex.getMessage());

        StandardResponse<Void> response = StandardResponse.<Void>builder()
                .timestamp(Instant.now())
                .status(ex.getStatus().value())
                .message(ex.getMessage())
                .errorCode(ex.getErrorCode().name())
                .path(request.getRequestURI())
                .traceId(traceId)
                .data(null)
                .build();

        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    @ExceptionHandler(feign.FeignException.class)
    public ResponseEntity<StandardResponse<Void>> handleFeignException(feign.FeignException ex, HttpServletRequest request) {
        String traceId = TraceIdGenerator.getTraceId();
        log.warn("[{}] FeignException: status={} message={}", traceId, ex.status(), ex.getMessage());

        HttpStatus status = HttpStatus.resolve(ex.status());
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return buildResponse(status, "Error calling external service", ErrorCode.EXTERNAL_SERVICE_UNAVAILABLE, request, traceId);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardResponse<Void>> handleGenericException(Exception ex, HttpServletRequest request) {
        String traceId = TraceIdGenerator.getTraceId();
        log.error("[{}] Unhandled exception on path {}: {}", traceId, request.getRequestURI(), ex.getMessage(), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred: " + ex.getMessage(), ErrorCode.INTERNAL_SERVER_ERROR, request, traceId);
    }

    private ResponseEntity<StandardResponse<Void>> buildResponse(HttpStatus status, String message, ErrorCode errorCode, HttpServletRequest request, String traceId) {
        StandardResponse<Void> response = StandardResponse.<Void>builder()
                .timestamp(Instant.now())
                .status(status.value())
                .message(message)
                .errorCode(errorCode.name())
                .path(request.getRequestURI())
                .traceId(traceId)
                .data(null)
                .build();

        return ResponseEntity.status(status).body(response);
    }
}
