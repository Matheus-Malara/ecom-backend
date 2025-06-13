package br.com.ecommerce.ecom.dto.responses;

import lombok.Data;

@Data
public class BrandResponseDTO {
    private Long id;
    private String name;
    private String description;
    private String logoUrl;
    private Boolean active;
}