package br.com.ecommerce.ecom.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse<T> {

    private Instant timestamp;
    private int status;
    private String message;
    private String path;
    private String traceId;
    private T data;
    private String errorCode;
}
