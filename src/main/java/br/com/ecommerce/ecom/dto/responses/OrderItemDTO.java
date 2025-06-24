package br.com.ecommerce.ecom.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@Schema(description = "Represents a product included in an order")
public class OrderItemDTO {

    @Schema(description = "Name of the product", example = "Whey Protein 900g")
    private String productName;

    @Schema(description = "Quantity of the product ordered", example = "2")
    private Integer quantity;

    @Schema(description = "Price per unit at the time of purchase", example = "89.90")
    private BigDecimal pricePerUnit;
}