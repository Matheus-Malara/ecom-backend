package br.com.ecommerce.ecom.factory;

import br.com.ecommerce.ecom.dto.responses.StandardResponse;
import br.com.ecommerce.ecom.enums.ErrorCode;
import br.com.ecommerce.ecom.util.TraceIdGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class ResponseFactory {

    public <T> ResponseEntity<StandardResponse<T>> okResponse(T data, String message, String path) {
        return ResponseEntity.ok(buildResponse(data, message, path, HttpStatus.OK, null));
    }

    public <T> ResponseEntity<StandardResponse<T>> createdResponse(T data, String message, String path) {
        return ResponseEntity.status(HttpStatus.CREATED).body(buildResponse(data, message, path, HttpStatus.CREATED, null));
    }

    public <T> ResponseEntity<StandardResponse<T>> noContentResponse(String message, String path) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(buildResponse(null, message, path, HttpStatus.NO_CONTENT, null));
    }

    public <T> ResponseEntity<StandardResponse<T>> errorResponse(T data, String message, String path, HttpStatus status, ErrorCode errorCode) {
        return ResponseEntity.status(status).body(buildResponse(data, message, path, status, errorCode));
    }

    public <T> ResponseEntity<StandardResponse<T>> acceptedResponse(T data, String message, String path) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(buildResponse(data, message, path, HttpStatus.ACCEPTED, null));
    }

    public <T> ResponseEntity<StandardResponse<T>> badRequestResponse(T data, String message, String path, ErrorCode errorCode) {
        return ResponseEntity.badRequest().body(buildResponse(data, message, path, HttpStatus.BAD_REQUEST, errorCode));
    }

    public <T> ResponseEntity<StandardResponse<T>> forbiddenResponse(String message, String path, ErrorCode errorCode) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(buildResponse(null, message, path, HttpStatus.FORBIDDEN, errorCode));
    }

    public <T> ResponseEntity<StandardResponse<T>> customResponse(T data, String message, String path, HttpStatus status, ErrorCode errorCode) {
        return ResponseEntity.status(status).body(buildResponse(data, message, path, status, errorCode));
    }

    private <T> StandardResponse<T> buildResponse(T data, String message, String path, HttpStatus status, ErrorCode errorCode) {
        return StandardResponse.<T>builder()
                .timestamp(Instant.now())
                .status(status.value())
                .message(message)
                .errorCode(errorCode != null ? errorCode.name() : null)
                .path(path)
                .traceId(TraceIdGenerator.getTraceId())
                .data(data)
                .build();
    }
}