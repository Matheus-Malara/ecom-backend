package br.com.ecommerce.ecom.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Payload to remove a product from the cart")
public class RemoveFromCartRequestDTO {

    @NotNull(message = "productId is required")
    @Schema(description = "ID of the product to remove", example = "1")
    private Long productId;
}
