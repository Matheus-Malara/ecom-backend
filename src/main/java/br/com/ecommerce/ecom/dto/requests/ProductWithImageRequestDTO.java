package br.com.ecommerce.ecom.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Data
@Schema(description = "Product creation request with optional image upload")
public class ProductWithImageRequestDTO {

    @NotBlank
    @Size(max = 150)
    @Schema(description = "Product name", example = "Whey Protein 900g", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Size(max = 1000)
    @Schema(description = "Product description", example = "Premium whey protein with high biological value")
    private String description;

    @NotNull
    @Schema(description = "Category ID", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long categoryId;

    @NotNull
    @Schema(description = "Brand ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long brandId;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Schema(description = "Product price", example = "149.99", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal price;

    @NotNull
    @Min(0)
    @Schema(description = "Stock quantity", example = "50", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer stock;

    @NotNull
    @Min(1)
    @Schema(description = "Product weight in grams", example = "900", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer weightGrams;

    @Size(max = 50)
    @Schema(description = "Flavor", example = "Chocolate")
    private String flavor;

    @Schema(description = "Image file (optional)", type = "string", format = "binary")
    private MultipartFile image;
}
