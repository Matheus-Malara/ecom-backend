package br.com.ecommerce.ecom.dto.filters;

import br.com.ecommerce.ecom.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "Filter options for searching orders")
public class OrderFilterDTO {

    @Schema(description = "Status of the order", example = "PENDING")
    private OrderStatus status;

    @Schema(description = "User email associated with the order", example = "customer@example.com")
    private String userEmail;

    @Schema(description = "Start date for order creation filter (inclusive)", example = "2024-01-01")
    private LocalDate startDate;

    @Schema(description = "End date for order creation filter (inclusive)", example = "2024-12-31")
    private LocalDate endDate;
}
