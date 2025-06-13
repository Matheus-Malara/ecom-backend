package br.com.ecommerce.ecom.dto.requests;

import lombok.Data;

@Data
public class BrandRequestDTO {
    private String name;
    private String description;
    private String logoUrl;
}