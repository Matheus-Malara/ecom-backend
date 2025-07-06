package br.com.ecommerce.ecom.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@Schema(description = "Cart data returned by the API")
public class CartResponseDTO {

    @Schema(description = "Cart ID", example = "1")
    private Long cartId;

    @Schema(description = "Anonymous ID for guest carts", example = "abc123")
    private String anonymousId;

    @Schema(description = "List of items in the cart")
    private List<CartItemDTO> items;

    @Schema(description = "Total number of items", example = "3")
    private Integer totalItems;

    @Schema(description = "Total amount of the cart", example = "249.90")
    private BigDecimal totalAmount;
}
