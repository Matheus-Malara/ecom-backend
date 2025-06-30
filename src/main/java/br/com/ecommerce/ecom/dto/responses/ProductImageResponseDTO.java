package br.com.ecommerce.ecom.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Image associated with a product")
public class ProductImageResponseDTO {

    @Schema(description = "Image ID", example = "12")
    private Long id;

    @Schema(description = "Image URL")
    private String imageUrl;

    @Schema(description = "Display order", example = "1")
    private Integer displayOrder;
}