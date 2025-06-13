package br.com.ecommerce.ecom.dto.requests;

import lombok.Data;

@Data
public class CategoryRequestDTO {
    private String name;
    private String description;
    private String imageUrl;
}