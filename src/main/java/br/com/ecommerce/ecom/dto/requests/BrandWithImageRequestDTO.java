package br.com.ecommerce.ecom.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Schema(description = "DTO for creating or updating a brand with an optional image upload")
public class BrandWithImageRequestDTO {

    @NotBlank(message = "Brand name is required")
    @Size(max = 100, message = "Brand name must not exceed 100 characters")
    @Schema(description = "Name of the brand", example = "Optimum Nutrition", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Schema(description = "Description of the brand", example = "High-quality supplement products")
    private String description;

    @Schema(description = "Brand image file (optional)", type = "string", format = "binary")
    private MultipartFile image;
}
