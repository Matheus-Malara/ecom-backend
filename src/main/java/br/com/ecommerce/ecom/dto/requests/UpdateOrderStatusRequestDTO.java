package br.com.ecommerce.ecom.dto.requests;

import br.com.ecommerce.ecom.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateOrderStatusRequestDTO {
    @NotNull(message = "Status is required")
    private OrderStatus status;
}