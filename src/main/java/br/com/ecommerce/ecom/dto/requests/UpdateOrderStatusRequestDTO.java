package br.com.ecommerce.ecom.dto.requests;

import br.com.ecommerce.ecom.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Request body to update the status of an order")
public class UpdateOrderStatusRequestDTO {

    @NotNull(message = "Status is required")
    @Schema(description = "New status for the order", example = "SHIPPED")
    private OrderStatus status;
}
