package br.com.ecommerce.ecom.dto.responses;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderItemDTO {
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
}