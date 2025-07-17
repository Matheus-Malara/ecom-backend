package br.com.ecommerce.ecom.dto.filters;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Filter criteria used for searching brands")
public class BrandFilterDTO {

    @Schema(description = "Filter by active status", example = "true")
    private Boolean active;

    @Schema(description = "Partial match for brand name", example = "Nike")
    private String name;
}