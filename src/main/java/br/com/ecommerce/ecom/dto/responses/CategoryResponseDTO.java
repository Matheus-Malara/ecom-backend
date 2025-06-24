package br.com.ecommerce.ecom.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Category information returned by the API")
public class CategoryResponseDTO {

    @Schema(description = "Category ID", example = "1")
    private Long id;

    @Schema(description = "Category name", example = "Supplements")
    private String name;

    @Schema(description = "Category description", example = "Nutritional and performance supplements")
    private String description;

    @Schema(description = "Image URL", example = "https://cdn.example.com/images/supplements.jpg")
    private String imageUrl;

    @Schema(description = "Indicates if the category is active", example = "true")
    private Boolean active;
}
