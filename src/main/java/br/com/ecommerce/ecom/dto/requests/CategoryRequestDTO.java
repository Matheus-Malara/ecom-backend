package br.com.ecommerce.ecom.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryRequestDTO {

    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name must be at most 50 characters long")
    private String name;

    @Size(max = 500, message = "Description must be at most 255 characters long")
    private String description;

    @Pattern(
            regexp = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$",
            message = "Image URL must be valid"
    )
    private String imageUrl;
}
