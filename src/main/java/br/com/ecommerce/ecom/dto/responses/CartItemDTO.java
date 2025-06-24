package br.com.ecommerce.ecom.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@Schema(description = "Represents a product added to the cart")
public class CartItemDTO {

    @Schema(description = "ID of the product", example = "42")
    private Long productId;

    @Schema(description = "Name of the product", example = "Whey Protein 900g")
    private String productName;

    @Schema(description = "Quantity of the product in the cart", example = "2")
    private Integer quantity;

    @Schema(description = "Price per unit", example = "89.90")
    private BigDecimal pricePerUnit;

    @Schema(description = "Total price for this item (price × quantity)", example = "179.80")
    private BigDecimal totalPrice;

    @Schema(description = "Image URL of the product", example = "https://cdn.example.com/images/whey.jpg")
    private String imageUrl;
}
