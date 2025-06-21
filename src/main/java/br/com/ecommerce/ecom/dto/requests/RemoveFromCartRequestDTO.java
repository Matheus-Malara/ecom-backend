package br.com.ecommerce.ecom.dto.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RemoveFromCartRequestDTO {
    @NotNull(message = "productId is required")
    private Long productId;
}
