package br.com.ecommerce.ecom.dto.responses;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


@Data
@Builder
public class CartResponseDTO {
    private Long cartId;
    private List<CartItemDTO> items;
    private Integer totalItems;
    private BigDecimal totalAmount;
}