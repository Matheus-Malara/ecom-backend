package br.com.ecommerce.ecom.exception;

import br.com.ecommerce.ecom.dto.responses.ApiError;
import br.com.ecommerce.ecom.dto.responses.ApiResponse;
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
    public ResponseEntity<ApiResponse<List<ApiError>>> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.warn("Validation error on path {}: {} field errors", request.getRequestURI(), ex.getBindingResult().getFieldErrors().size());

        List<ApiError> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> ApiError.builder()
                        .field(error.getField())
                        .message(error.getDefaultMessage())
                        .build())
                .toList();

        ApiResponse<List<ApiError>> response = ApiResponse.<List<ApiError>>builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Validation failed")
                .errorCode(ErrorCode.VALIDATION_FAILED.name())
                .path(request.getRequestURI())
                .traceId(TraceIdGenerator.getTraceId())
                .data(errors)
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        log.warn("Method not supported: {} on {}", ex.getMethod(), request.getRequestURI());
        return buildResponse(HttpStatus.METHOD_NOT_ALLOWED, "HTTP method not allowed", ErrorCode.INVALID_REQUEST, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleMalformedJson(HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.warn("Malformed JSON request: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "Malformed request body", ErrorCode.INVALID_REQUEST, request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        log.warn("Access denied on path {} on {}", ex.getMessage(), request.getRequestURI());
        return buildResponse(HttpStatus.FORBIDDEN, "Access denied", ErrorCode.ACCESS_DENIED, request);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        log.warn("Resource not found: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), ErrorCode.RESOURCE_NOT_FOUND, request);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        log.warn("BusinessException: {} - {} - {}", ex.getStatus(), ex.getErrorCode(), ex.getMessage());

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .timestamp(Instant.now())
                .status(ex.getStatus().value())
                .message(ex.getMessage())
                .errorCode(ex.getErrorCode().name())
                .path(request.getRequestURI())
                .traceId(TraceIdGenerator.getTraceId())
                .data(null)
                .build();

        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    @ExceptionHandler(feign.FeignException.class)
    public ResponseEntity<ApiResponse<Void>> handleFeignException(feign.FeignException ex, HttpServletRequest request) {
        log.warn("FeignException: status={} message={}", ex.status(), ex.getMessage());

        HttpStatus status = HttpStatus.resolve(ex.status());
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return buildResponse(status, "Error calling external service", ErrorCode.EXTERNAL_SERVICE_UNAVAILABLE, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception: ", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred: " + ex.getMessage(), ErrorCode.INTERNAL_SERVER_ERROR, request);
    }

    private ResponseEntity<ApiResponse<Void>> buildResponse(HttpStatus status, String message, ErrorCode errorCode, HttpServletRequest request) {
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .timestamp(Instant.now())
                .status(status.value())
                .message(message)
                .errorCode(errorCode.name())
                .path(request.getRequestURI())
                .traceId(TraceIdGenerator.getTraceId())
                .data(null)
                .build();

        return ResponseEntity.status(status).body(response);
    }
}
