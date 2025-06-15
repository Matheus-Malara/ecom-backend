package br.com.ecommerce.ecom.dto.filters;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryFilterDTO {

    private Boolean active;

    @Size(min = 2, message = "Search term must have at least 2 characters")
    private String name;
}