package br.com.ecommerce.ecom.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Brand information returned by the API")
public class BrandResponseDTO {

    @Schema(description = "Brand ID", example = "1")
    private Long id;

    @Schema(description = "Brand name", example = "Nike")
    private String name;

    @Schema(description = "Brand description", example = "Worldwide sportswear brand")
    private String description;

    @Schema(description = "URL to the brand's logo", example = "https://cdn.example.com/brands/nike.svg")
    private String logoUrl;

    @Schema(description = "Indicates if the brand is active", example = "true")
    private Boolean active;
}
