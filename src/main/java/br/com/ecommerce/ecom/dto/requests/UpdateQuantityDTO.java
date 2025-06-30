package br.com.ecommerce.ecom.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
@Schema(description = "Payload to update the quantity of an item in the cart")
public class UpdateQuantityDTO {

    @Min(value = 1, message = "quantity must be at least 1")
    @Schema(description = "New quantity", example = "3")
    private int quantity;
}
