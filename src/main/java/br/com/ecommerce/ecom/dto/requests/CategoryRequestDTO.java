package br.com.ecommerce.ecom.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Payload to create or update a category")
public class CategoryRequestDTO {

    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name must be at most 100 characters long")
    @Schema(description = "Category name", example = "Supplements")
    private String name;

    @Size(max = 500, message = "Description must be at most 500 characters long")
    @Schema(description = "Category description", example = "Nutritional and performance supplements")
    private String description;

    @Pattern(
            regexp = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$",
            message = "Image URL must be valid"
    )
    @Schema(description = "URL of the category image", example = "https://cdn.example.com/images/supplements.jpg")
    private String imageUrl;
}
