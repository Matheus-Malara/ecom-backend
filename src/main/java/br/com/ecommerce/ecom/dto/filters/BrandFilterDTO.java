package br.com.ecommerce.ecom.dto.filters;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrandFilterDTO {

    private Boolean active;

    @Size(min = 2, message = "Search term must have at least 2 characters")
    private String name;
}