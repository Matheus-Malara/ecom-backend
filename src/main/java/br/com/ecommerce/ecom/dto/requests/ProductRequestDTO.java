package br.com.ecommerce.ecom.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "Payload to create or update a product")
public class ProductRequestDTO {

    @NotBlank
    @Size(max = 150)
    @Schema(description = "Product name", example = "Whey Protein Isolate 900g")
    private String name;

    @Size(max = 1000)
    @Schema(description = "Description of the product", example = "Contains 90% protein concentration")
    private String description;

    @NotNull
    @Schema(description = "Category ID", example = "1")
    private Long categoryId;

    @NotNull
    @Schema(description = "Brand ID", example = "1")
    private Long brandId;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Schema(description = "Product price", example = "139.90")
    private BigDecimal price;

    @NotNull
    @Min(0)
    @Schema(description = "Available stock", example = "100")
    private Integer stock;

    @NotNull
    @Min(1)
    @Schema(description = "Weight in grams", example = "900")
    private Integer weightGrams;

    @Size(max = 50)
    @Schema(description = "Flavor of the product", example = "Vanilla")
    private String flavor;

    @Size(max = 10)
    @Schema(description = "List of product image URLs")
    private List<
            @NotBlank
            @Pattern(regexp = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$")
                    String> imageUrls;
}
