package br.com.ecommerce.ecom.dto.requests;

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
public class ProductRequestDTO {

    @NotBlank(message = "Product name is required")
    @Size(max = 150, message = "Product name must be at most 150 characters long")
    private String name;

    @Size(max = 1000, message = "Description must be at most 1000 characters long")
    private String description;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotNull(message = "Brand ID is required")
    private Long brandId;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
    private BigDecimal price;

    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;

    @NotNull(message = "Weight in grams is required")
    @Min(value = 1, message = "Weight must be greater than zero")
    private Integer weightGrams;

    @Size(max = 50, message = "Flavor must be at most 50 characters long")
    private String flavor;

    @Size(max = 10, message = "A maximum of 10 images is allowed")
    private List<
            @NotBlank(message = "Image URL cannot be blank")
            @Pattern(
                    regexp = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$",
                    message = "Image URL must be valid"
            )
                    String
            > imageUrls;
}