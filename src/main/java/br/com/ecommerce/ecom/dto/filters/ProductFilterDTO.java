package br.com.ecommerce.ecom.dto.filters;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductFilterDTO {

    @Size(max = 100, message = "Name filter must be at most 100 characters long")
    private String name;

    private Long categoryId;

    private Long brandId;

    private Boolean active;

    @Size(max = 50, message = "Flavor filter must be at most 50 characters long")
    private String flavor;

    @DecimalMin(value = "0.0", inclusive = false, message = "Minimum price must be greater than 0")
    private BigDecimal minPrice;

    @DecimalMin(value = "0.0", inclusive = false, message = "Maximum price must be greater than 0")
    private BigDecimal maxPrice;
}