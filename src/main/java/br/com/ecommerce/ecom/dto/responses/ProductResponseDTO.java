package br.com.ecommerce.ecom.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "Product data returned by the API")
public class ProductResponseDTO {

    @Schema(description = "Product ID", example = "1")
    private Long id;

    @Schema(description = "Product name", example = "Whey Protein 900g")
    private String name;

    @Schema(description = "Description of the product", example = "High quality whey protein with 25g per dose")
    private String description;

    @Schema(description = "Category name", example = "Supplements")
    private String categoryName;

    @Schema(description = "Brand name", example = "Growth")
    private String brandName;

    @Schema(description = "Product price", example = "129.90")
    private BigDecimal price;

    @Schema(description = "Available stock", example = "120")
    private Integer stock;

    @Schema(description = "Weight in grams", example = "900")
    private Integer weightGrams;

    @Schema(description = "Flavor of the product", example = "Chocolate")
    private String flavor;

    @Schema(description = "List of product images with ID and URL")
    private List<ProductImageResponseDTO> images;

    @Schema(description = "Indicates if the product is active", example = "true")
    private Boolean active;
}
