package br.com.ecommerce.ecom.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Payload to add a product to the cart")
public class AddToCartRequestDTO {

    @NotNull(message = "productId is required")
    @Schema(description = "ID of the product to add", example = "1")
    private Long productId;

    @Min(value = 1, message = "quantity must be at least 1")
    @Schema(description = "Quantity to add", example = "2")
    private int quantity;
}
