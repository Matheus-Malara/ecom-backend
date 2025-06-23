package br.com.ecommerce.ecom.dto.filters;

import br.com.ecommerce.ecom.enums.OrderStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class OrderFilterDTO {
    private OrderStatus status;
    private String userEmail;
    private LocalDate startDate;
    private LocalDate endDate;
}