package br.com.ecommerce.ecom.dto.filters;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Filter criteria used to search products")
public class ProductFilterDTO {

    @Size(max = 100)
    @Schema(description = "Name filter (partial match)", example = "whey")
    private String name;

    @Schema(description = "Filter by category ID", example = "2")
    private Long categoryId;

    @Schema(description = "Filter by brand ID", example = "3")
    private Long brandId;

    @Schema(description = "Filter by active status", example = "true")
    private Boolean active;

    @Size(max = 50)
    @Schema(description = "Flavor filter", example = "Chocolate")
    private String flavor;

    @DecimalMin(value = "0.0", inclusive = false)
    @Schema(description = "Minimum price filter", example = "50.00")
    private BigDecimal minPrice;

    @DecimalMin(value = "0.0", inclusive = false)
    @Schema(description = "Maximum price filter", example = "150.00")
    private BigDecimal maxPrice;
}
