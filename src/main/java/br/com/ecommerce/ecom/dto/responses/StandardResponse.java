package br.com.ecommerce.ecom.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Standard response wrapper for all API responses")
public class StandardResponse<T> {

    @Schema(description = "Timestamp when the response was generated", example = "2025-06-24T18:00:00Z")
    private Instant timestamp;

    @Schema(description = "HTTP status code of the response", example = "200")
    private int status;

    @Schema(description = "Human-readable message related to the operation", example = "Brand created successfully")
    private String message;

    @Schema(description = "Request path that originated this response", example = "/api/brands")
    private String path;

    @Schema(description = "Trace ID for distributed tracing", example = "abc123-def456")
    private String traceId;

    @Schema(description = "Payload of the response")
    private T data;

    @Schema(description = "Custom error code if an error occurred", example = "BRAND_NOT_FOUND")
    private String errorCode;
}