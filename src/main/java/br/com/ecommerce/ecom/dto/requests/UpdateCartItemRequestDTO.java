package br.com.ecommerce.ecom.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Payload to update the quantity of an item in the cart")
public class UpdateCartItemRequestDTO {

    @NotNull(message = "productId is required")
    @Schema(description = "ID of the product to update", example = "1")
    private Long productId;

    @Min(value = 1, message = "quantity must be at least 1")
    @Schema(description = "New quantity", example = "3")
    private int quantity;
}
