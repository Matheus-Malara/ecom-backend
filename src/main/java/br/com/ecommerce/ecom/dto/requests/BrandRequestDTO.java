package br.com.ecommerce.ecom.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BrandRequestDTO {

    @NotBlank(message = "Brand name is required")
    @Size(max = 100, message = "Brand name must be at most 100 characters long")
    private String name;

    @Size(max = 500, message = "Description must be at most 500 characters long")
    private String description;

    @Pattern(
            regexp = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$",
            message = "Logo URL must be valid"
    )
    private String logoUrl;
}
