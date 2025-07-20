package br.com.ecommerce.ecom.dto.filters;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Filter criteria used to search categories")
public class CategoryFilterDTO {

    @Schema(description = "Filter by active status", example = "true")
    private Boolean active;

    @Schema(description = "Partial name filter", example = "supp")
    private String name;
}
