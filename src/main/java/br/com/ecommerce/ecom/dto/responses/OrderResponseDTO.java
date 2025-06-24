package br.com.ecommerce.ecom.dto.responses;

import br.com.ecommerce.ecom.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Schema(description = "Order information returned by the API")
public class OrderResponseDTO {

    @Schema(description = "Order ID", example = "1")
    private Long id;

    @Schema(description = "Total amount of the order", example = "249.90")
    private BigDecimal totalAmount;

    @Schema(description = "Current status of the order", example = "PENDING")
    private OrderStatus status;

    @Schema(description = "Date and time the order was created", example = "2025-06-24T18:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "List of items in the order")
    private List<OrderItemDTO> items;
}
