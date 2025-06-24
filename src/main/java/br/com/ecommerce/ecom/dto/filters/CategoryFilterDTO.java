package br.com.ecommerce.ecom.dto.filters;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Filter criteria used to search categories")
public class CategoryFilterDTO {

    @Schema(description = "Filter by active status", example = "true")
    private Boolean active;

    @Size(min = 2, message = "Search term must have at least 2 characters")
    @Schema(description = "Partial name filter", example = "supp")
    private String name;
}
