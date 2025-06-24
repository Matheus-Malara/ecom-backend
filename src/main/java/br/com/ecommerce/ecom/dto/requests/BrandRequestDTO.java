package br.com.ecommerce.ecom.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Payload to create or update a brand")
public class BrandRequestDTO {

    @NotBlank(message = "Brand name is required")
    @Size(max = 100, message = "Brand name must be at most 100 characters long")
    @Schema(description = "Brand name", example = "Nike")
    private String name;

    @Size(max = 500, message = "Description must be at most 500 characters long")
    @Schema(description = "Brand description", example = "Worldwide sportswear brand")
    private String description;

    @Pattern(
            regexp = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$",
            message = "Logo URL must be valid"
    )
    @Schema(description = "URL to the brand's logo image", example = "https://cdn.example.com/brands/nike.svg")
    private String logoUrl;
}
